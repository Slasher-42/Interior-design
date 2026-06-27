package com.ced.Feedback.Communication.Service.event;

import com.ced.Feedback.Communication.Service.domain.CommunicationChannel;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import com.ced.Feedback.Communication.Service.service.FeedbackService;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import com.ced.Feedback.Communication.Service.service.ProjectInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectTaskEventConsumer {

    private final NotificationService notificationService;
    private final CommunicationLogService communicationLogService;
    private final ProjectInfoService projectInfoService;
    private final FeedbackService feedbackService;
    private final JsonMapper kafkaEventJsonMapper;

    @KafkaListener(topics = KafkaTopics.PROJECT_CREATED)
    public void onProjectCreated(String payload) {
        try {
            ProjectCreatedEvent event = kafkaEventJsonMapper.readValue(payload, ProjectCreatedEvent.class);
            projectInfoService.upsert(event.getProjectId(), event.getClientId(), event.getProjectManagerId());

            String clientMessage = "Your project has been created!";
            notificationService.notifyUser(event.getClientId(), "Project created", clientMessage, event.getProjectId());
            communicationLogService.log(event.getClientId(), CommunicationChannel.NOTIFICATION, "Project created", clientMessage);

            String staffMessage = "A new project is ready for setup.";
            UUID projectManagerId = event.getProjectManagerId();
            if (projectManagerId != null) {
                notificationService.notifyUser(projectManagerId, "New project", staffMessage, event.getProjectId());
            } else {
                notificationService.broadcastToRole(Role.PROJECT_MANAGER, "New project", staffMessage, event.getProjectId());
            }
            notificationService.broadcastToRole(Role.ADMIN, "New project", staffMessage, event.getProjectId());
        } catch (Exception e) {
            log.error("Failed to process project.created event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.TASK_ASSIGNED)
    public void onTaskAssigned(String payload) {
        try {
            TaskAssignedEvent event = kafkaEventJsonMapper.readValue(payload, TaskAssignedEvent.class);
            String message = "You have been assigned a new task: " + event.getTitle();
            notificationService.notifyUser(event.getAssignedUserId(), "New task assigned", message, event.getProjectId());
        } catch (Exception e) {
            log.error("Failed to process task.assigned event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.TASK_COMPLETED)
    public void onTaskCompleted(String payload) {
        try {
            TaskCompletedEvent event = kafkaEventJsonMapper.readValue(payload, TaskCompletedEvent.class);
            String message = "A task has been completed on one of your projects.";
            UUID projectManagerId = projectInfoService.findProjectManagerId(event.getProjectId());
            if (projectManagerId != null) {
                notificationService.notifyUser(projectManagerId, "Task completed", message, event.getProjectId());
            } else {
                notificationService.broadcastToRole(Role.PROJECT_MANAGER, "Task completed", message, event.getProjectId());
            }
        } catch (Exception e) {
            log.error("Failed to process task.completed event: {}", payload, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PROJECT_COMPLETED)
    public void onProjectCompleted(String payload) {
        try {
            ProjectCompletedEvent event = kafkaEventJsonMapper.readValue(payload, ProjectCompletedEvent.class);
            String message = "Your project is complete! We'd love your feedback.";
            notificationService.notifyUser(event.getClientId(), "Project completed", message, event.getProjectId());
            communicationLogService.log(event.getClientId(), CommunicationChannel.NOTIFICATION, "Project completed", message);
            feedbackService.openFeedbackRequest(event.getProjectId(), event.getClientId());
        } catch (Exception e) {
            log.error("Failed to process project.completed event: {}", payload, e);
        }
    }
}
