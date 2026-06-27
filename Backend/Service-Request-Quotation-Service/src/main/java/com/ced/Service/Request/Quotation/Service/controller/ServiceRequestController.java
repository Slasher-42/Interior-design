package com.ced.Service.Request.Quotation.Service.controller;

import com.ced.Service.Request.Quotation.Service.domain.Priority;
import com.ced.Service.Request.Quotation.Service.domain.Role;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequest;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequestStatus;
import com.ced.Service.Request.Quotation.Service.dto.*;
import com.ced.Service.Request.Quotation.Service.security.CurrentUser;
import com.ced.Service.Request.Quotation.Service.service.QuotationService;
import com.ced.Service.Request.Quotation.Service.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> submit(@Valid @RequestBody CreateServiceRequestRequest request) {
        ServiceRequest serviceRequest = serviceRequestService.submit(CurrentUser.id(), CurrentUser.role(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ServiceRequestResponse.from(serviceRequest));
    }

    @GetMapping
    public ResponseEntity<Page<ServiceRequestResponse>> search(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID assignedDesignerId,
            @RequestParam(required = false) ServiceRequestStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID effectiveClientId = CurrentUser.role() == Role.CLIENT ? CurrentUser.id() : clientId;
        Page<ServiceRequestResponse> result = serviceRequestService
                .search(effectiveClientId, assignedDesignerId, status, priority, category, PageRequest.of(page, size))
                .map(ServiceRequestResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponse> getById(@PathVariable UUID id) {
        ServiceRequest serviceRequest = serviceRequestService.getByIdForCaller(id, CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(ServiceRequestResponse.from(serviceRequest));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ServiceRequestResponse> assign(@PathVariable UUID id,
                                                          @Valid @RequestBody AssignRequestRequest request) {
        ServiceRequest serviceRequest = serviceRequestService.assign(id, request.designerId());
        return ResponseEntity.ok(ServiceRequestResponse.from(serviceRequest));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ServiceRequestResponse> reject(@PathVariable UUID id,
                                                          @Valid @RequestBody RejectRequestRequest request) {
        ServiceRequest serviceRequest = serviceRequestService.reject(id, request.reason());
        return ResponseEntity.ok(ServiceRequestResponse.from(serviceRequest));
    }

    @PostMapping("/{id}/quotations")
    public ResponseEntity<QuotationResponse> createQuotation(@PathVariable UUID id,
                                                              @Valid @RequestBody CreateQuotationRequest request) {
        QuotationResponse response = QuotationResponse.from(quotationService.create(id, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
