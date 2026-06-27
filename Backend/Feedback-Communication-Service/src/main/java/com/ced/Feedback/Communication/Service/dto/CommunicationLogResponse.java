package com.ced.Feedback.Communication.Service.dto;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.domain.CommunicationLog;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CommunicationLogResponse(
        UUID id,
        UUID clientId,
        CommunicationChannel channel,
        String subject,
        String message,
        Instant createdAt
) {
    public static CommunicationLogResponse from(CommunicationLog c) {
        return CommunicationLogResponse.builder()
                .id(c.getId())
                .clientId(c.getClientId())
                .channel(c.getChannel())
                .subject(c.getSubject())
                .message(c.getMessage())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
