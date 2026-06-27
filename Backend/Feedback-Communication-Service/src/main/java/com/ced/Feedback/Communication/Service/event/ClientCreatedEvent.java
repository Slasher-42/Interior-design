package com.ced.Feedback.Communication.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by User-Client-Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreatedEvent {
    private UUID clientId;
    private String name;
    private String email;
    private String phone;
    private Instant createdAt;
}
