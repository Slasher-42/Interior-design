package com.ced.Reporting.Analytics.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Project & Task Service.
 */
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
