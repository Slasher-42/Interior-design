package com.ced.Auth.Security.Service.dto;

import com.ced.Auth.Security.Service.domain.Role;
import com.ced.Auth.Security.Service.domain.User;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        Role role,
        boolean verified,
        boolean enabled,
        boolean mfaEnabled,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .verified(user.isVerified())
                .enabled(user.isEnabled())
                .mfaEnabled(user.isMfaEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
