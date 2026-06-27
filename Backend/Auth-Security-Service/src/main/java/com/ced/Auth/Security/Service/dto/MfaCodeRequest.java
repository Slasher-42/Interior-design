package com.ced.Auth.Security.Service.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaCodeRequest(
        @NotBlank String code
) {
}
