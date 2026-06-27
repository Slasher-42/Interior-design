package com.ced.Project.Task.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProjectCreated(ProjectCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.PROJECT_CREATED, event.getProjectId().toString(), event);
    }

    public void publishTaskAssigned(TaskAssignedEvent event) {
        kafkaTemplate.send(KafkaTopics.TASK_ASSIGNED, event.getTaskId().toString(), event);
    }

    public void publishTaskCompleted(TaskCompletedEvent event) {
        kafkaTemplate.send(KafkaTopics.TASK_COMPLETED, event.getTaskId().toString(), event);
    }

    public void publishProjectCompleted(ProjectCompletedEvent event) {
        kafkaTemplate.send(KafkaTopics.PROJECT_COMPLETED, event.getProjectId().toString(), event);
    }
}
