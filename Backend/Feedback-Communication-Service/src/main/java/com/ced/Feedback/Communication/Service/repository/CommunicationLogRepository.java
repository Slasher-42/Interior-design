package com.ced.Feedback.Communication.Service.repository;

import com.ced.Feedback.Communication.Service.domain.CommunicationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, UUID> {

    Page<CommunicationLog> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);
}
