package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import com.ced.Feedback.Communication.Service.service.ProjectInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentEventConsumer {

    private final NotificationService notificationService;
    private final CommunicationLogService communicationLogService;
    private final ProjectInfoService projectInfoService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.DOCUMENT_APPROVED)
    public void onDocumentApproved(String payload) {
        try {
            DocumentApprovedEvent event = kafkaEventJsonMapper.readValue(payload, DocumentApprovedEvent.class);
            UUID clientId = projectInfoService.findClientId(event.getProjectId());
            if (clientId == null) {
                log.warn("No cached client found for project {} while processing document.approved", event.getProjectId());
                return;
            }
            String message = "A new design is ready for your review.";
            notificationService.notifyUser(clientId, "New design ready", message, event.getProjectId());
            communicationLogService.log(clientId, CommunicationChannel.NOTIFICATION, "New design ready", message);
        } catch (Exception e) {
            log.error("Failed to process document.approved event: {}", payload, e);
        }
    }
}
