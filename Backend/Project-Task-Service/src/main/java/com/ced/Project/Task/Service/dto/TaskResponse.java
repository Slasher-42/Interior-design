package com.ced.Project.Task.Service.dto;

import com.ced.Project.Task.Service.domain.Priority;
import com.ced.Project.Task.Service.domain.Task;
import com.ced.Project.Task.Service.domain.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TaskResponse(
        UUID id,
        UUID projectId,
        UUID milestoneId,
        String title,
        String description,
        UUID assignedUserId,
        Priority priority,
        TaskStatus status,
        LocalDate deadline,
        Instant completedAt,
        Instant createdAt
) {
    public static TaskResponse from(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .projectId(t.getProjectId())
                .milestoneId(t.getMilestoneId())
                .title(t.getTitle())
                .description(t.getDescription())
                .assignedUserId(t.getAssignedUserId())
                .priority(t.getPriority())
                .status(t.getStatus())
                .deadline(t.getDeadline())
                .completedAt(t.getCompletedAt())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
