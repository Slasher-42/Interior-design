package com.ced.Service.Request.Quotation.Service.service;

import com.ced.Service.Request.Quotation.Service.domain.Quotation;
import com.ced.Service.Request.Quotation.Service.domain.QuotationStatus;
import com.ced.Service.Request.Quotation.Service.domain.Role;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequest;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequestStatus;
import com.ced.Service.Request.Quotation.Service.dto.CreateQuotationRequest;
import com.ced.Service.Request.Quotation.Service.event.KafkaEventPublisher;
import com.ced.Service.Request.Quotation.Service.event.QuotationApprovedEvent;
import com.ced.Service.Request.Quotation.Service.event.QuotationCreatedEvent;
import com.ced.Service.Request.Quotation.Service.exception.AppException;
import com.ced.Service.Request.Quotation.Service.repository.QuotationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final ServiceRequestService serviceRequestService;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final QuotationPdfService quotationPdfService;

    @Transactional
    public Quotation create(UUID requestId, CreateQuotationRequest request) {
        ServiceRequest serviceRequest = serviceRequestService.getById(requestId);
        if (serviceRequest.getStatus() != ServiceRequestStatus.ASSIGNED) {
            throw new AppException("Quotations can only be created for assigned requests", HttpStatus.CONFLICT);
        }

        BigDecimal total = request.materialCost().add(request.laborCost()).add(request.additionalCharges());

        Quotation quotation = Quotation.builder()
                .requestId(requestId)
                .clientId(serviceRequest.getClientId())
                .materialCost(request.materialCost())
                .laborCost(request.laborCost())
                .additionalCharges(request.additionalCharges())
                .totalAmount(total)
                .build();
        quotation = quotationRepository.save(quotation);

        quotation.setPdfPath(quotationPdfService.generate(quotation, serviceRequest));
        quotation = quotationRepository.save(quotation);

        kafkaEventPublisher.publishQuotationCreated(QuotationCreatedEvent.builder()
                .quotationId(quotation.getId())
                .requestId(requestId)
                .clientId(quotation.getClientId())
                .totalAmount(quotation.getTotalAmount())
                .status(quotation.getStatus())
                .createdAt(quotation.getCreatedAt())
                .build());

        return quotation;
    }

    @Transactional
    public Quotation approve(UUID id, UUID callerId, Role callerRole) {
        Quotation quotation = getByIdForCaller(id, callerId, callerRole);
        if (quotation.getStatus() != QuotationStatus.PENDING_APPROVAL) {
            throw new AppException("Only pending quotations can be approved", HttpStatus.CONFLICT);
        }
        quotation.setStatus(QuotationStatus.APPROVED);
        quotation.setApprovedAt(Instant.now());
        quotation = quotationRepository.save(quotation);

        serviceRequestService.close(quotation.getRequestId());

        kafkaEventPublisher.publishQuotationApproved(QuotationApprovedEvent.builder()
                .quotationId(quotation.getId())
                .requestId(quotation.getRequestId())
                .clientId(quotation.getClientId())
                .totalAmount(quotation.getTotalAmount())
                .approvedAt(quotation.getApprovedAt())
                .build());

        return quotation;
    }

    @Transactional
    public Quotation reject(UUID id, UUID callerId, Role callerRole, String reason) {
        Quotation quotation = getByIdForCaller(id, callerId, callerRole);
        if (quotation.getStatus() != QuotationStatus.PENDING_APPROVAL) {
            throw new AppException("Only pending quotations can be rejected", HttpStatus.CONFLICT);
        }
        quotation.setStatus(QuotationStatus.REJECTED);
        quotation.setRejectedAt(Instant.now());
        quotation.setRejectionReason(reason);
        return quotationRepository.save(quotation);
    }

    public Quotation getById(UUID id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new AppException("Quotation not found", HttpStatus.NOT_FOUND));
    }

    public Quotation getByIdForCaller(UUID id, UUID callerId, Role callerRole) {
        Quotation quotation = getById(id);
        assertOwnership(quotation, callerId, callerRole);
        return quotation;
    }

    public byte[] getPdf(UUID id, UUID callerId, Role callerRole) {
        Quotation quotation = getByIdForCaller(id, callerId, callerRole);
        return quotationPdfService.read(quotation.getPdfPath());
    }

    public Page<Quotation> search(UUID clientId, UUID requestId, QuotationStatus status, Pageable pageable) {
        Specification<Quotation> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (clientId != null) {
                predicates.add(cb.equal(root.get("clientId"), clientId));
            }
            if (requestId != null) {
                predicates.add(cb.equal(root.get("requestId"), requestId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return quotationRepository.findAll(spec, pageable);
    }

    private void assertOwnership(Quotation quotation, UUID callerId, Role callerRole) {
        if (callerRole == Role.CLIENT && !quotation.getClientId().equals(callerId)) {
            throw new AppException("Quotation not found", HttpStatus.NOT_FOUND);
        }
    }
}
