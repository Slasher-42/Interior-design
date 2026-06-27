package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestQuotationEventConsumer {

    private final NotificationService notificationService;
    private final CommunicationLogService communicationLogService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.SERVICE_REQUEST_CREATED)
    public void onServiceRequestCreated(String payload) {
        try {
            ServiceRequestCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ServiceRequestCreatedEvent.class);
            String message = "New " + event.getPriority() + " priority request: " + event.getCategory();
            notificationService.broadcastToRole(Role.SALES_TEAM, "New service request", message, event.getRequestId());
            notificationService.broadcastToRole(Role.ADMIN, "New service request", message, event.getRequestId());
        } catch (Exception e) {
            log.error("Failed to process service.request.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.SERVICE_REQUEST_ASSIGNED)
    public void onServiceRequestAssigned(String payload) {
        try {
            ServiceRequestAssignedEvent event = kafkaEventJsonMapper.readValue(payload, ServiceRequestAssignedEvent.class);
            notificationService.notifyUser(event.getAssignedDesignerId(), "New assignment",
                    "You have been assigned to a service request.", event.getRequestId());

            String clientMessage = "A designer has been assigned to your request.";
            notificationService.notifyUser(event.getClientId(), "Designer assigned", clientMessage, event.getRequestId());
            communicationLogService.log(event.getClientId(), CommunicationChannel.NOTIFICATION, "Designer assigned", clientMessage);
        } catch (Exception e) {
            log.error("Failed to process service.request.assigned event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.QUOTATION_CREATED)
    public void onQuotationCreated(String payload) {
        try {
            QuotationCreatedEvent event = kafkaEventJsonMapper.readValue(payload, QuotationCreatedEvent.class);
            String message = "Your quotation (" + event.getTotalAmount() + ") is ready for review.";
            notificationService.notifyUser(event.getClientId(), "Quotation ready", message, event.getQuotationId());
            communicationLogService.log(event.getClientId(), CommunicationChannel.NOTIFICATION, "Quotation ready", message);
        } catch (Exception e) {
            log.error("Failed to process quotation.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.QUOTATION_APPROVED)
    public void onQuotationApproved(String payload) {
        try {
            QuotationApprovedEvent event = kafkaEventJsonMapper.readValue(payload, QuotationApprovedEvent.class);
            String message = "A quotation has been approved and a project will be created.";
            notificationService.broadcastToRole(Role.PROJECT_MANAGER, "Quotation approved", message, event.getQuotationId());
            notificationService.broadcastToRole(Role.DESIGNER, "Quotation approved", message, event.getQuotationId());
        } catch (Exception e) {
            log.error("Failed to process quotation.approved event: {}", payload, e);
        }
    }
}
