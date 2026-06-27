package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.ProcurementAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendorEventConsumer {

    private final ProcurementAnalyticsService procurementAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.PURCHASE_ORDER_CREATED)
    public void onPurchaseOrderCreated(String payload) {
        try {
            PurchaseOrderCreatedEvent event = kafkaEventJsonMapper.readValue(payload, PurchaseOrderCreatedEvent.class);
            procurementAnalyticsService.recordCreated(event.getOrderId(), event.getProjectId(),
                    event.getVendorId(), event.getEstimatedCost(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process purchase.order.created event: {}", payload, e);
        }
    }
}
