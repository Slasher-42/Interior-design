package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import com.ced.Feedback.Communication.Service.service.EmailService;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final CommunicationLogService communicationLogService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.CLIENT_CREATED)
    public void onClientCreated(String payload) {
        try {
            ClientCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ClientCreatedEvent.class);
            String subject = "Welcome to IDSMS";
            String inPlatformMessage = "Welcome! Your client profile has been created.";

            notificationService.notifyUser(event.getClientId(), subject, inPlatformMessage, null);
            communicationLogService.log(event.getClientId(), CommunicationChannel.NOTIFICATION, subject, inPlatformMessage);

            if (StringUtils.hasText(event.getEmail())) {
                String body = "Hi " + event.getName() + ",\n\nWelcome! Your account with our interior design team has been created.";
                emailService.send(event.getEmail(), subject, body);
                communicationLogService.log(event.getClientId(), CommunicationChannel.EMAIL, subject, body);
            }
        } catch (Exception e) {
            log.error("Failed to process client.created event: {}", payload, e);
        }
    }
}
