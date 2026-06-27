package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import com.ced.Feedback.Communication.Service.service.EmailService;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final CommunicationLogService communicationLogService;
    private final JsonMapper kafkaEventJsonMapper;

    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED)
    public void onUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = kafkaEventJsonMapper.readValue(payload, UserRegisteredEvent.class);
            String subject = "Verify your IDSMS account";
            String link = frontendBaseUrl + "/verify?token=" + event.getVerificationToken();
            String body = "Hi " + event.getFullName() + ",\n\nPlease verify your account: " + link;

            notificationService.notifyUser(event.getUserId(), subject, "Please verify your email to activate your account.", null);
            emailService.send(event.getEmail(), subject, body);
            if (event.getRole() == Role.CLIENT) {
                communicationLogService.log(event.getUserId(), CommunicationChannel.EMAIL, subject, body);
            }
        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.USER_VERIFIED)
    public void onUserVerified(String payload) {
        try {
            UserVerifiedEvent event = kafkaEventJsonMapper.readValue(payload, UserVerifiedEvent.class);
            String subject = "Welcome to IDSMS";
            String body = "Hi " + event.getFullName() + ",\n\nYour account is verified. Welcome aboard!";

            notificationService.notifyUser(event.getUserId(), subject, "Your account is now verified.", null);
            emailService.send(event.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to process user.verified event: {}", payload, e);
        }
    }
}
