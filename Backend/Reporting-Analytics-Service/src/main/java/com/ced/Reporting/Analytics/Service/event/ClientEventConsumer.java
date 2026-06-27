package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.ClientAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final ClientAnalyticsService clientAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.CLIENT_CREATED)
    public void onClientCreated(String payload) {
        try {
            ClientCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ClientCreatedEvent.class);
            clientAnalyticsService.recordClientCreated(event.getClientId(), event.getName(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process client.created event: {}", payload, e);
        }
    }
}
