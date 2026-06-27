package com.ced.Reporting.Analytics.Service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRecord {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    private UUID projectManagerId;

    @Column(precision = 12, scale = 2)
    private BigDecimal approvedBudget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectRecordStatus status = ProjectRecordStatus.ACTIVE;

    @Column(precision = 12, scale = 2)
    private BigDecimal finalCost;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant completedAt;
}
