package com.ced.Reporting.Analytics.Service.dto;

import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecord;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ServiceRequestSummary(
        UUID id,
        UUID clientId,
        String category,
        String priority,
        Instant createdAt
) {
    public static ServiceRequestSummary from(ServiceRequestRecord r) {
        return ServiceRequestSummary.builder()
                .id(r.getId())
                .clientId(r.getClientId())
                .category(r.getCategory())
                .priority(r.getPriority().name())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
