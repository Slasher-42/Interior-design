package com.ced.Auth.Security.Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    private int passwordMinLength = 8;

    @Builder.Default
    private boolean passwordRequireUppercase = true;

    @Builder.Default
    private boolean passwordRequireNumber = true;

    @Builder.Default
    private boolean passwordRequireSpecialChar = true;

    @Builder.Default
    private int sessionTimeoutMinutes = 60;

    @Builder.Default
    private int maxFailedLoginAttempts = 5;

    @Builder.Default
    private int accountLockMinutes = 15;

    @Builder.Default
    private boolean mfaEnforced = false;

    @Builder.Default
    private boolean maintenanceMode = false;

    private Instant updatedAt;

    private UUID updatedBy;

    @PrePersist
    @PreUpdate
    void onSave() {
        updatedAt = Instant.now();
    }
}
