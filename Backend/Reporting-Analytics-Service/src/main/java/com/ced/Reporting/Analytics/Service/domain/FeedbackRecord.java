package com.ced.Reporting.Analytics.Service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "feedback_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRecord {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    @Builder.Default
    private boolean lowRatingFlag = false;

    @Column(nullable = false)
    private Instant submittedAt;
}
