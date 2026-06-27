package com.ced.User.Client.Service.dto;

import com.ced.User.Client.Service.domain.ClientInteraction;
import com.ced.User.Client.Service.domain.InteractionType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ClientInteractionResponse(
        UUID id,
        UUID clientId,
        InteractionType type,
        UUID referenceId,
        String description,
        Instant occurredAt
) {
    public static ClientInteractionResponse from(ClientInteraction i) {
        return ClientInteractionResponse.builder()
                .id(i.getId())
                .clientId(i.getClientId())
                .type(i.getType())
                .referenceId(i.getReferenceId())
                .description(i.getDescription())
                .occurredAt(i.getOccurredAt())
                .build();
    }
}
