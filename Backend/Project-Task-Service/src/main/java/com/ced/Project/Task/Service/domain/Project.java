package com.ced.Project.Task.Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private UUID requestId;

    @Column(nullable = false)
    private UUID quotationId;

    /**
     * Null until an Admin/PM completes setup. The quotation.approved event that creates this
     * project does not carry a project manager or schedule, only client/request/quotation
     * identifiers and the approved budget.
     */
    private UUID projectManagerId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal approvedBudget;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal materialCost = BigDecimal.ZERO;

    @Builder.Default
    private boolean budgetOverrun = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING;

    private BigDecimal finalCost;

    private Instant completedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
