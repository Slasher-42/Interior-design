package com.ced.User.Client.Service.event;

import com.ced.User.Client.Service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ProfileService profileService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED)
    public void onUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = kafkaEventJsonMapper.readValue(payload, UserRegisteredEvent.class);
            profileService.handleUserRegistered(event);
        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.USER_VERIFIED)
    public void onUserVerified(String payload) {
        try {
            UserVerifiedEvent event = kafkaEventJsonMapper.readValue(payload, UserVerifiedEvent.class);
            profileService.handleUserVerified(event);
        } catch (Exception e) {
            log.error("Failed to process user.verified event: {}", payload, e);
        }
    }
}
