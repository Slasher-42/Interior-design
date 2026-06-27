package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.AuditLog;
import com.ced.Auth.Security.Service.event.AuditLoggedEvent;
import com.ced.Auth.Security.Service.event.KafkaEventPublisher;
import com.ced.Auth.Security.Service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public void log(UUID userId, AuditAction action, String description, String ipAddress) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .description(description)
                .ipAddress(ipAddress)
                .build();
        AuditLog saved = auditLogRepository.save(entry);

        kafkaEventPublisher.publishAuditLogged(AuditLoggedEvent.builder()
                .userId(saved.getUserId())
                .action(saved.getAction())
                .description(saved.getDescription())
                .ipAddress(saved.getIpAddress())
                .timestamp(saved.getTimestamp())
                .build());
    }

    public Page<AuditLog> search(UUID userId, AuditAction action, Pageable pageable) {
        if (userId != null && action != null) {
            return auditLogRepository.findByUserIdAndAction(userId, action, pageable);
        }
        if (userId != null) {
            return auditLogRepository.findByUserId(userId, pageable);
        }
        if (action != null) {
            return auditLogRepository.findByAction(action, pageable);
        }
        return auditLogRepository.findAll(pageable);
    }
}
