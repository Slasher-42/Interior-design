package com.ced.Project.Task.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletedEvent {
    private UUID taskId;
    private UUID projectId;
    private UUID milestoneId;
    private UUID assignedUserId;
    private Instant completedAt;
}
