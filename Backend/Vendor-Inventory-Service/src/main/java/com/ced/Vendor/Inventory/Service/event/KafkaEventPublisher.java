package com.ced.Vendor.Inventory.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPurchaseOrderCreated(PurchaseOrderCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.PURCHASE_ORDER_CREATED, event.getOrderId().toString(), event);
    }
}
