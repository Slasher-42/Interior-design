package com.ced.Feedback.Communication.Service.dto;

import com.ced.Feedback.Communication.Service.domain.Feedback;
import com.ced.Feedback.Communication.Service.domain.FeedbackStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FeedbackResponse(
        UUID id,
        UUID projectId,
        UUID clientId,
        FeedbackStatus status,
        Integer overallRating,
        Integer designQualityRating,
        Integer responsivenessRating,
        Integer timelineAdherenceRating,
        String comments,
        String complaint,
        boolean hasComplaint,
        Instant submittedAt,
        Instant createdAt
) {
    public static FeedbackResponse from(Feedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .projectId(f.getProjectId())
                .clientId(f.getClientId())
                .status(f.getStatus())
                .overallRating(f.getOverallRating())
                .designQualityRating(f.getDesignQualityRating())
                .responsivenessRating(f.getResponsivenessRating())
                .timelineAdherenceRating(f.getTimelineAdherenceRating())
                .comments(f.getComments())
                .complaint(f.getComplaint())
                .hasComplaint(f.isHasComplaint())
                .submittedAt(f.getSubmittedAt())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
