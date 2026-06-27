package com.ced.Feedback.Communication.Service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitFeedbackRequest(
        @NotNull @Min(1) @Max(5) Integer overallRating,
        @NotNull @Min(1) @Max(5) Integer designQualityRating,
        @NotNull @Min(1) @Max(5) Integer responsivenessRating,
        @NotNull @Min(1) @Max(5) Integer timelineAdherenceRating,
        String comments,
        String complaint
) {
}
