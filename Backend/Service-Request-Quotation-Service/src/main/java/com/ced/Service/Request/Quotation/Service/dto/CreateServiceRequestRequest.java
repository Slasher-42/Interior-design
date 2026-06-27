package com.ced.Service.Request.Quotation.Service.dto;

import com.ced.Service.Request.Quotation.Service.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateServiceRequestRequest(
        UUID clientId,
        @NotBlank String category,
        @NotBlank String description,
        @NotNull Priority priority
) {
}
