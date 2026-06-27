package com.ced.Feedback.Communication.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFeedbackSubmitted(FeedbackSubmittedEvent event) {
        kafkaTemplate.send(KafkaTopics.FEEDBACK_SUBMITTED, event.getFeedbackId().toString(), event);
    }
}
