package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.FeedbackAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackEventConsumer {

    private final FeedbackAnalyticsService feedbackAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.FEEDBACK_SUBMITTED)
    public void onFeedbackSubmitted(String payload) {
        try {
            FeedbackSubmittedEvent event = kafkaEventJsonMapper.readValue(payload, FeedbackSubmittedEvent.class);
            feedbackAnalyticsService.recordSubmitted(event.getFeedbackId(), event.getClientId(),
                    event.getProjectId(), event.getRating(), event.getSubmittedAt());
        } catch (Exception e) {
            log.error("Failed to process feedback.submitted event: {}", payload, e);
        }
    }
}
