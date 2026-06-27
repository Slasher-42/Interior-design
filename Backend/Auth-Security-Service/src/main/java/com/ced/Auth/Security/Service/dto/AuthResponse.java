package com.ced.Auth.Security.Service.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresInMs,
        String userId,
        String role,
        boolean mfaRequired,
        boolean mfaSetupRequired,
        String challengeToken
) {
}
