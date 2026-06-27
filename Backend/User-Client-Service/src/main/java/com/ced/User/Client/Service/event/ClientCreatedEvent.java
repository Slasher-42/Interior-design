package com.ced.User.Client.Service.event;

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
public class ClientCreatedEvent {
    private UUID clientId;
    private String name;
    private String email;
    private String phone;
    private Instant createdAt;
}
