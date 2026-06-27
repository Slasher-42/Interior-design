package com.ced.Feedback.Communication.Service.dto;

import com.ced.Feedback.Communication.Service.domain.EmailLog;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record EmailLogResponse(
        UUID id,
        String recipientEmail,
        String subject,
        String body,
        boolean success,
        Instant sentAt
) {
    public static EmailLogResponse from(EmailLog e) {
        return EmailLogResponse.builder()
                .id(e.getId())
                .recipientEmail(e.getRecipientEmail())
                .subject(e.getSubject())
                .body(e.getBody())
                .success(e.isSuccess())
                .sentAt(e.getSentAt())
                .build();
    }
}
