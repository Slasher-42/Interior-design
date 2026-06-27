package com.ced.Reporting.Analytics.Service.dto;

import com.ced.Reporting.Analytics.Service.domain.ProjectRecord;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ProjectSummary(
        UUID id,
        UUID clientId,
        String status,
        BigDecimal approvedBudget,
        BigDecimal finalCost,
        Instant createdAt,
        Instant completedAt
) {
    public static ProjectSummary from(ProjectRecord p) {
        return ProjectSummary.builder()
                .id(p.getId())
                .clientId(p.getClientId())
                .status(p.getStatus().name())
                .approvedBudget(p.getApprovedBudget())
                .finalCost(p.getFinalCost())
                .createdAt(p.getCreatedAt())
                .completedAt(p.getCompletedAt())
                .build();
    }
}
