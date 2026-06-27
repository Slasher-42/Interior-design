package com.ced.Reporting.Analytics.Service.dto;

import com.ced.Reporting.Analytics.Service.domain.FeedbackRecord;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FeedbackSummaryResponse(
        UUID id,
        UUID projectId,
        UUID clientId,
        Integer rating,
        Instant submittedAt
) {
    public static FeedbackSummaryResponse from(FeedbackRecord f) {
        return FeedbackSummaryResponse.builder()
                .id(f.getId())
                .projectId(f.getProjectId())
                .clientId(f.getClientId())
                .rating(f.getRating())
                .submittedAt(f.getSubmittedAt())
                .build();
    }
}
