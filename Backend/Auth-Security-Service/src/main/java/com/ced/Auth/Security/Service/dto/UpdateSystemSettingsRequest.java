package com.ced.Auth.Security.Service.dto;

import jakarta.validation.constraints.Min;

public record UpdateSystemSettingsRequest(
        @Min(4) Integer passwordMinLength,
        Boolean passwordRequireUppercase,
        Boolean passwordRequireNumber,
        Boolean passwordRequireSpecialChar,
        @Min(1) Integer sessionTimeoutMinutes,
        @Min(1) Integer maxFailedLoginAttempts,
        @Min(1) Integer accountLockMinutes,
        Boolean mfaEnforced,
        Boolean maintenanceMode
) {
}
