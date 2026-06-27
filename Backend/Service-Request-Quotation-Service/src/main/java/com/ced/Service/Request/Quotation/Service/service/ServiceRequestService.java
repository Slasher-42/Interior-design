package com.ced.Service.Request.Quotation.Service.service;

import com.ced.Service.Request.Quotation.Service.domain.Priority;
import com.ced.Service.Request.Quotation.Service.domain.Role;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequest;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequestStatus;
import com.ced.Service.Request.Quotation.Service.dto.CreateServiceRequestRequest;
import com.ced.Service.Request.Quotation.Service.event.KafkaEventPublisher;
import com.ced.Service.Request.Quotation.Service.event.ServiceRequestAssignedEvent;
import com.ced.Service.Request.Quotation.Service.event.ServiceRequestCreatedEvent;
import com.ced.Service.Request.Quotation.Service.exception.AppException;
import com.ced.Service.Request.Quotation.Service.repository.ServiceRequestRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public ServiceRequest submit(UUID callerId, Role callerRole, CreateServiceRequestRequest request) {
        UUID clientId = resolveClientId(callerId, callerRole, request.clientId());

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .clientId(clientId)
                .category(request.category())
                .description(request.description())
                .priority(request.priority())
                .build();
        serviceRequest = serviceRequestRepository.save(serviceRequest);

        kafkaEventPublisher.publishServiceRequestCreated(ServiceRequestCreatedEvent.builder()
                .requestId(serviceRequest.getId())
                .clientId(clientId)
                .category(serviceRequest.getCategory())
                .priority(serviceRequest.getPriority())
                .description(serviceRequest.getDescription())
                .createdAt(serviceRequest.getCreatedAt())
                .build());

        return serviceRequest;
    }

    @Transactional
    public ServiceRequest assign(UUID id, UUID designerId) {
        ServiceRequest serviceRequest = getById(id);
        if (serviceRequest.getStatus() != ServiceRequestStatus.PENDING) {
            throw new AppException("Only pending requests can be assigned", HttpStatus.CONFLICT);
        }
        serviceRequest.setAssignedDesignerId(designerId);
        serviceRequest.setAssignedAt(Instant.now());
        serviceRequest.setStatus(ServiceRequestStatus.ASSIGNED);
        serviceRequest = serviceRequestRepository.save(serviceRequest);

        kafkaEventPublisher.publishServiceRequestAssigned(ServiceRequestAssignedEvent.builder()
                .requestId(serviceRequest.getId())
                .clientId(serviceRequest.getClientId())
                .assignedDesignerId(designerId)
                .assignedAt(serviceRequest.getAssignedAt())
                .build());

        return serviceRequest;
    }

    @Transactional
    public ServiceRequest reject(UUID id, String reason) {
        ServiceRequest serviceRequest = getById(id);
        if (serviceRequest.getStatus() != ServiceRequestStatus.PENDING) {
            throw new AppException("Only pending requests can be rejected", HttpStatus.CONFLICT);
        }
        serviceRequest.setStatus(ServiceRequestStatus.REJECTED);
        serviceRequest.setRejectionReason(reason);
        return serviceRequestRepository.save(serviceRequest);
    }

    @Transactional
    void close(UUID id) {
        ServiceRequest serviceRequest = getById(id);
        serviceRequest.setStatus(ServiceRequestStatus.CLOSED);
        serviceRequestRepository.save(serviceRequest);
    }

    public ServiceRequest getById(UUID id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new AppException("Service request not found", HttpStatus.NOT_FOUND));
    }

    public ServiceRequest getByIdForCaller(UUID id, UUID callerId, Role callerRole) {
        ServiceRequest serviceRequest = getById(id);
        assertOwnership(serviceRequest, callerId, callerRole);
        return serviceRequest;
    }

    public Page<ServiceRequest> search(UUID clientId, UUID assignedDesignerId, ServiceRequestStatus status,
                                        Priority priority, String category, Pageable pageable) {
        Specification<ServiceRequest> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (clientId != null) {
                predicates.add(cb.equal(root.get("clientId"), clientId));
            }
            if (assignedDesignerId != null) {
                predicates.add(cb.equal(root.get("assignedDesignerId"), assignedDesignerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (StringUtils.hasText(category)) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return serviceRequestRepository.findAll(spec, pageable);
    }

    private UUID resolveClientId(UUID callerId, Role callerRole, UUID requestedClientId) {
        if (callerRole == Role.CLIENT) {
            return callerId;
        }
        if (requestedClientId == null) {
            throw new AppException("clientId is required when submitting on behalf of a client", HttpStatus.BAD_REQUEST);
        }
        return requestedClientId;
    }

    private void assertOwnership(ServiceRequest serviceRequest, UUID callerId, Role callerRole) {
        if (callerRole == Role.CLIENT && !serviceRequest.getClientId().equals(callerId)) {
            throw new AppException("Service request not found", HttpStatus.NOT_FOUND);
        }
    }
}
