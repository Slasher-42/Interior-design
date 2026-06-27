package com.ced.Auth.Security.Service.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaVerifyRequest(
        @NotBlank String challengeToken,
        @NotBlank String code
) {
}
