package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.QuotationAnalyticsService;
import com.ced.Reporting.Analytics.Service.service.ServiceRequestAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestQuotationEventConsumer {

    private final ServiceRequestAnalyticsService serviceRequestAnalyticsService;
    private final QuotationAnalyticsService quotationAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.SERVICE_REQUEST_CREATED)
    public void onServiceRequestCreated(String payload) {
        try {
            ServiceRequestCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ServiceRequestCreatedEvent.class);
            serviceRequestAnalyticsService.recordCreated(event.getRequestId(), event.getClientId(),
                    event.getCategory(), event.getPriority(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process service.request.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.SERVICE_REQUEST_ASSIGNED)
    public void onServiceRequestAssigned(String payload) {
        try {
            ServiceRequestAssignedEvent event = kafkaEventJsonMapper.readValue(payload, ServiceRequestAssignedEvent.class);
            serviceRequestAnalyticsService.recordAssigned(event.getRequestId());
        } catch (Exception e) {
            log.error("Failed to process service.request.assigned event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.QUOTATION_CREATED)
    public void onQuotationCreated(String payload) {
        try {
            QuotationCreatedEvent event = kafkaEventJsonMapper.readValue(payload, QuotationCreatedEvent.class);
            quotationAnalyticsService.recordCreated(event.getQuotationId(), event.getRequestId(),
                    event.getClientId(), event.getTotalAmount(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process quotation.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.QUOTATION_APPROVED)
    public void onQuotationApproved(String payload) {
        try {
            QuotationApprovedEvent event = kafkaEventJsonMapper.readValue(payload, QuotationApprovedEvent.class);
            quotationAnalyticsService.recordApproved(event.getQuotationId(), event.getApprovedAt());
        } catch (Exception e) {
            log.error("Failed to process quotation.approved event: {}", payload, e);
        }
    }
}
