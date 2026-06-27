package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.UserAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventConsumer {

    private final UserAnalyticsService userAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED)
    public void onUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = kafkaEventJsonMapper.readValue(payload, UserRegisteredEvent.class);
            userAnalyticsService.recordSignup(event.getUserId(), event.getRole(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", payload, e);
        }
    }
}
