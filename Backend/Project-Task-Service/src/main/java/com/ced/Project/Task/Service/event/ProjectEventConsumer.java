package com.ced.Project.Task.Service.event;

import com.ced.Project.Task.Service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventConsumer {

    private final ProjectService projectService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.QUOTATION_APPROVED)
    public void onQuotationApproved(String payload) {
        try {
            QuotationApprovedEvent event = kafkaEventJsonMapper.readValue(payload, QuotationApprovedEvent.class);
            projectService.handleQuotationApproved(event);
        } catch (Exception e) {
            log.error("Failed to process quotation.approved event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PURCHASE_ORDER_CREATED)
    public void onPurchaseOrderCreated(String payload) {
        try {
            PurchaseOrderCreatedEvent event = kafkaEventJsonMapper.readValue(payload, PurchaseOrderCreatedEvent.class);
            projectService.handlePurchaseOrderCreated(event);
        } catch (Exception e) {
            log.error("Failed to process purchase.order.created event: {}", payload, e);
        }
    }
}
