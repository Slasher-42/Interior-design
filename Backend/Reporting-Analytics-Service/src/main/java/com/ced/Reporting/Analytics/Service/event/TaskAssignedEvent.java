package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.domain.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Local mirror of the event published by Project & Task Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedEvent {
    private UUID taskId;
    private UUID projectId;
    private UUID assignedUserId;
    private String title;
    private Priority priority;
    private LocalDate deadline;
}
