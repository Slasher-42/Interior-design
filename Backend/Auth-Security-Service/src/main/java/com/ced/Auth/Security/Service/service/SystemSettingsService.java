package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.SystemSettings;
import com.ced.Auth.Security.Service.dto.UpdateSystemSettingsRequest;
import com.ced.Auth.Security.Service.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;
    private final AuditService auditService;

    @Transactional
    public SystemSettings getOrCreate() {
        return systemSettingsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> systemSettingsRepository.save(SystemSettings.builder().build()));
    }

    @Transactional
    public SystemSettings update(UpdateSystemSettingsRequest request, UUID adminId, String ipAddress) {
        SystemSettings settings = getOrCreate();
        boolean maintenanceChanged = false;

        if (request.passwordMinLength() != null) settings.setPasswordMinLength(request.passwordMinLength());
        if (request.passwordRequireUppercase() != null) settings.setPasswordRequireUppercase(request.passwordRequireUppercase());
        if (request.passwordRequireNumber() != null) settings.setPasswordRequireNumber(request.passwordRequireNumber());
        if (request.passwordRequireSpecialChar() != null) settings.setPasswordRequireSpecialChar(request.passwordRequireSpecialChar());
        if (request.sessionTimeoutMinutes() != null) settings.setSessionTimeoutMinutes(request.sessionTimeoutMinutes());
        if (request.maxFailedLoginAttempts() != null) settings.setMaxFailedLoginAttempts(request.maxFailedLoginAttempts());
        if (request.accountLockMinutes() != null) settings.setAccountLockMinutes(request.accountLockMinutes());
        if (request.mfaEnforced() != null) settings.setMfaEnforced(request.mfaEnforced());
        if (request.maintenanceMode() != null && request.maintenanceMode() != settings.isMaintenanceMode()) {
            settings.setMaintenanceMode(request.maintenanceMode());
            maintenanceChanged = true;
        }

        settings.setUpdatedBy(adminId);
        SystemSettings saved = systemSettingsRepository.save(settings);

        auditService.log(adminId, AuditAction.SETTINGS_UPDATED, "System settings updated", ipAddress);
        if (maintenanceChanged) {
            auditService.log(adminId, AuditAction.MAINTENANCE_MODE_TOGGLED,
                    "Maintenance mode set to " + saved.isMaintenanceMode(), ipAddress);
        }

        return saved;
    }
}
