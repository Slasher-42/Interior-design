package com.ced.Service.Request.Quotation.Service.dto;

import com.ced.Service.Request.Quotation.Service.domain.Priority;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequest;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequestStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ServiceRequestResponse(
        UUID id,
        UUID clientId,
        String category,
        String description,
        Priority priority,
        ServiceRequestStatus status,
        UUID assignedDesignerId,
        Instant assignedAt,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static ServiceRequestResponse from(ServiceRequest r) {
        return ServiceRequestResponse.builder()
                .id(r.getId())
                .clientId(r.getClientId())
                .category(r.getCategory())
                .description(r.getDescription())
                .priority(r.getPriority())
                .status(r.getStatus())
                .assignedDesignerId(r.getAssignedDesignerId())
                .assignedAt(r.getAssignedAt())
                .rejectionReason(r.getRejectionReason())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
