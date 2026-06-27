package com.ced.Reporting.Analytics.Service.dto;

import com.ced.Reporting.Analytics.Service.domain.TaskRecord;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TaskSummary(
        UUID id,
        UUID projectId,
        String priority,
        String status,
        LocalDate deadline,
        Instant completedAt
) {
    public static TaskSummary from(TaskRecord t) {
        return TaskSummary.builder()
                .id(t.getId())
                .projectId(t.getProjectId())
                .priority(t.getPriority().name())
                .status(t.getStatus().name())
                .deadline(t.getDeadline())
                .completedAt(t.getCompletedAt())
                .build();
    }
}
