package com.ced.Auth.Security.Service.repository;

import com.ced.Auth.Security.Service.domain.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, UUID> {
}
