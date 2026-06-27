package com.ced.Project.Task.Service.event;

import com.ced.Project.Task.Service.domain.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

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
