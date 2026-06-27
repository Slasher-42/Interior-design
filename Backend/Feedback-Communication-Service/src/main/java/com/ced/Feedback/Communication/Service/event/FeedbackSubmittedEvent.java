package com.ced.Feedback.Communication.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSubmittedEvent {
    private UUID feedbackId;
    private UUID clientId;
    private UUID projectId;
    private Integer rating;
    private Instant submittedAt;
}
