package com.ced.Feedback.Communication.Service.dto;

import com.ced.Feedback.Communication.Service.domain.Notification;
import com.ced.Feedback.Communication.Service.domain.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NotificationResponse(
        UUID id,
        UUID recipientId,
        Role recipientRole,
        String title,
        String message,
        UUID referenceId,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .recipientId(n.getRecipientId())
                .recipientRole(n.getRecipientRole())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
