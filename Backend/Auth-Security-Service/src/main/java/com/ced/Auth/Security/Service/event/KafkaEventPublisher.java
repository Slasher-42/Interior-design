package com.ced.Auth.Security.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_REGISTERED, event.getUserId().toString(), event);
    }

    public void publishUserVerified(UserVerifiedEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_VERIFIED, event.getUserId().toString(), event);
    }

    public void publishPasswordResetRequested(PasswordResetRequestedEvent event) {
        kafkaTemplate.send(KafkaTopics.PASSWORD_RESET_REQUESTED, event.getUserId().toString(), event);
    }

    public void publishAuditLogged(AuditLoggedEvent event) {
        String key = event.getUserId() != null ? event.getUserId().toString() : "system";
        kafkaTemplate.send(KafkaTopics.AUTH_AUDIT_LOGGED, key, event);
    }
}
