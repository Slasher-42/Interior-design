package com.ced.User.Client.Service.dto;

import com.ced.User.Client.Service.domain.InteractionType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RecordInteractionRequest(
        @NotNull InteractionType type,
        UUID referenceId,
        String description
) {
}
