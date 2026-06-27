package com.ced.Auth.Security.Service.dto;

import lombok.Builder;

@Builder
public record MfaSetupResponse(
        String secret,
        String otpAuthUrl
) {
}
