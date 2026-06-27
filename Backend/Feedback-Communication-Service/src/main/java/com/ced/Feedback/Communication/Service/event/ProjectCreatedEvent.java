package com.ced.Feedback.Communication.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Local mirror of the event published by Project & Task Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreatedEvent {
    private UUID projectId;
    private UUID clientId;
    private UUID requestId;
    private UUID quotationId;
    private UUID projectManagerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal approvedBudget;
    private Instant createdAt;
}
