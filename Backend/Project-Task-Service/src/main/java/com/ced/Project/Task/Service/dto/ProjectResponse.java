package com.ced.Project.Task.Service.dto;

import com.ced.Project.Task.Service.domain.Project;
import com.ced.Project.Task.Service.domain.ProjectStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ProjectResponse(
        UUID id,
        UUID clientId,
        UUID requestId,
        UUID quotationId,
        UUID projectManagerId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal approvedBudget,
        BigDecimal materialCost,
        boolean budgetOverrun,
        ProjectStatus status,
        BigDecimal finalCost,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse from(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .clientId(p.getClientId())
                .requestId(p.getRequestId())
                .quotationId(p.getQuotationId())
                .projectManagerId(p.getProjectManagerId())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .approvedBudget(p.getApprovedBudget())
                .materialCost(p.getMaterialCost())
                .budgetOverrun(p.isBudgetOverrun())
                .status(p.getStatus())
                .finalCost(p.getFinalCost())
                .completedAt(p.getCompletedAt())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
