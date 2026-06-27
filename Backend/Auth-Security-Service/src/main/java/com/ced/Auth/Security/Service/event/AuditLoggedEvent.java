package com.ced.Auth.Security.Service.event;

import com.ced.Auth.Security.Service.domain.AuditAction;
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
public class AuditLoggedEvent {
    private UUID userId;
    private AuditAction action;
    private String description;
    private String ipAddress;
    private Instant timestamp;
}
