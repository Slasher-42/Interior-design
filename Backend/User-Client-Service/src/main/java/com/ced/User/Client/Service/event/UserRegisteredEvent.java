package com.ced.User.Client.Service.event;

import com.ced.User.Client.Service.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Auth-Security-Service. Each service defines its
 * own copy of the contract rather than sharing a Java class across service boundaries.
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
