package com.ced.Auth.Security.Service.dto;

import com.ced.Auth.Security.Service.domain.SystemSettings;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SystemSettingsResponse(
        int passwordMinLength,
        boolean passwordRequireUppercase,
        boolean passwordRequireNumber,
        boolean passwordRequireSpecialChar,
        int sessionTimeoutMinutes,
        int maxFailedLoginAttempts,
        int accountLockMinutes,
        boolean mfaEnforced,
        boolean maintenanceMode,
        Instant updatedAt
) {
    public static SystemSettingsResponse from(SystemSettings s) {
        return SystemSettingsResponse.builder()
                .passwordMinLength(s.getPasswordMinLength())
                .passwordRequireUppercase(s.isPasswordRequireUppercase())
                .passwordRequireNumber(s.isPasswordRequireNumber())
                .passwordRequireSpecialChar(s.isPasswordRequireSpecialChar())
                .sessionTimeoutMinutes(s.getSessionTimeoutMinutes())
                .maxFailedLoginAttempts(s.getMaxFailedLoginAttempts())
                .accountLockMinutes(s.getAccountLockMinutes())
                .mfaEnforced(s.isMfaEnforced())
                .maintenanceMode(s.isMaintenanceMode())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
