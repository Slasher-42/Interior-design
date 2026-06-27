package com.ced.Feedback.Communication.Service.repository;

import com.ced.Feedback.Communication.Service.domain.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
}
