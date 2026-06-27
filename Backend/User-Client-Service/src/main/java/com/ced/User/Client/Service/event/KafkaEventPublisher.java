package com.ced.User.Client.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishClientCreated(ClientCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.CLIENT_CREATED, event.getClientId().toString(), event);
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_DELETED, event.getUserId().toString(), event);
    }
}
