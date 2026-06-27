package com.ced.Service.Request.Quotation.Service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignRequestRequest(
        @NotNull UUID designerId
) {
}
