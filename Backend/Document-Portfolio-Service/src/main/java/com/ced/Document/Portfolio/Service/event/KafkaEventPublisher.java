package com.ced.Document.Portfolio.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDocumentApproved(DocumentApprovedEvent event) {
        kafkaTemplate.send(KafkaTopics.DOCUMENT_APPROVED, event.getDocumentId().toString(), event);
    }
}
