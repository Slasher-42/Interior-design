package com.ced.Auth.Security.Service.dto;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.AuditLog;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AuditLogResponse(
        UUID id,
        UUID userId,
        AuditAction action,
        String description,
        String ipAddress,
        Instant timestamp
) {
    public static AuditLogResponse from(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .action(log.getAction())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .timestamp(log.getTimestamp())
                .build();
    }
}
