package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Auth-Security-Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private UUID userId;
    private String email;
    private String fullName;
    private Role role;
    private String verificationToken;
    private Instant createdAt;
}
