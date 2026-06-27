package com.ced.Service.Request.Quotation.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishServiceRequestCreated(ServiceRequestCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.SERVICE_REQUEST_CREATED, event.getRequestId().toString(), event);
    }

    public void publishServiceRequestAssigned(ServiceRequestAssignedEvent event) {
        kafkaTemplate.send(KafkaTopics.SERVICE_REQUEST_ASSIGNED, event.getRequestId().toString(), event);
    }

    public void publishQuotationCreated(QuotationCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.QUOTATION_CREATED, event.getQuotationId().toString(), event);
    }

    public void publishQuotationApproved(QuotationApprovedEvent event) {
        kafkaTemplate.send(KafkaTopics.QUOTATION_APPROVED, event.getQuotationId().toString(), event);
    }
}
