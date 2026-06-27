package com.ced.Auth.Security.Service.repository;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByUserIdAndAction(UUID userId, AuditAction action, Pageable pageable);
}
