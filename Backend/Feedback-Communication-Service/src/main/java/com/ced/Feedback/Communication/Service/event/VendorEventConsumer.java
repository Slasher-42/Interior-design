package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendorEventConsumer {

    private final NotificationService notificationService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.PURCHASE_ORDER_CREATED)
    public void onPurchaseOrderCreated(String payload) {
        try {
            PurchaseOrderCreatedEvent event = kafkaEventJsonMapper.readValue(payload, PurchaseOrderCreatedEvent.class);
            String message = "A new purchase order (" + event.getEstimatedCost() + ") has been raised.";
            notificationService.broadcastToRole(Role.ADMIN, "New purchase order", message, event.getProjectId());
        } catch (Exception e) {
            log.error("Failed to process purchase.order.created event: {}", payload, e);
        }
    }
}
