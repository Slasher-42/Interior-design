package com.ced.Feedback.Communication.Service.service;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.domain.CommunicationLog;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.exception.AppException;
import com.ced.Feedback.Communication.Service.repository.CommunicationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunicationLogService {

    private final CommunicationLogRepository communicationLogRepository;

    @Transactional
    public void log(UUID clientId, CommunicationChannel channel, String subject, String message) {
        communicationLogRepository.save(CommunicationLog.builder()
                .clientId(clientId)
                .channel(channel)
                .subject(subject)
                .message(message)
                .build());
    }

    public Page<CommunicationLog> listByClient(UUID clientId, UUID callerId, Role callerRole, Pageable pageable) {
        if (callerRole == Role.CLIENT && !clientId.equals(callerId)) {
            throw new AppException("Client not found", HttpStatus.NOT_FOUND);
        }
        return communicationLogRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable);
    }
}
