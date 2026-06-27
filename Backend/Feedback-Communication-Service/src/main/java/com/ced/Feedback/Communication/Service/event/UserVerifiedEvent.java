package com.ced.Feedback.Communication.Service.event;

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
public class UserVerifiedEvent {
    private UUID userId;
    private String email;
    private String fullName;
    private Instant verifiedAt;
}
