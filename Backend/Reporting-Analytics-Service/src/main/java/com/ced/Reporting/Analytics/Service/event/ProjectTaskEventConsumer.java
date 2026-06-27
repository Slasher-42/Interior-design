package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.service.ProjectAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectTaskEventConsumer {

    private final ProjectAnalyticsService projectAnalyticsService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.PROJECT_CREATED)
    public void onProjectCreated(String payload) {
        try {
            ProjectCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ProjectCreatedEvent.class);
            projectAnalyticsService.recordProjectCreated(event.getProjectId(), event.getClientId(),
                    event.getProjectManagerId(), event.getApprovedBudget(), event.getCreatedAt());
        } catch (Exception e) {
            log.error("Failed to process project.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.TASK_ASSIGNED)
    public void onTaskAssigned(String payload) {
        try {
            TaskAssignedEvent event = kafkaEventJsonMapper.readValue(payload, TaskAssignedEvent.class);
            projectAnalyticsService.recordTaskAssigned(event.getTaskId(), event.getProjectId(),
                    event.getAssignedUserId(), event.getPriority(), event.getDeadline());
        } catch (Exception e) {
            log.error("Failed to process task.assigned event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.TASK_COMPLETED)
    public void onTaskCompleted(String payload) {
        try {
            TaskCompletedEvent event = kafkaEventJsonMapper.readValue(payload, TaskCompletedEvent.class);
            projectAnalyticsService.recordTaskCompleted(event.getTaskId(), event.getCompletedAt());
        } catch (Exception e) {
            log.error("Failed to process task.completed event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PROJECT_COMPLETED)
    public void onProjectCompleted(String payload) {
        try {
            ProjectCompletedEvent event = kafkaEventJsonMapper.readValue(payload, ProjectCompletedEvent.class);
            projectAnalyticsService.recordProjectCompleted(event.getProjectId(), event.getFinalCost(), event.getCompletedAt());
        } catch (Exception e) {
            log.error("Failed to process project.completed event: {}", payload, e);
        }
    }
}
