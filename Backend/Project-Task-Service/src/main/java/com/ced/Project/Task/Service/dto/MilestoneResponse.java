package com.ced.Project.Task.Service.dto;

import com.ced.Project.Task.Service.domain.Milestone;
import com.ced.Project.Task.Service.domain.MilestoneStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record MilestoneResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        LocalDate dueDate,
        MilestoneStatus status,
        Instant completedAt,
        Instant createdAt
) {
    public static MilestoneResponse from(Milestone m) {
        return MilestoneResponse.builder()
                .id(m.getId())
                .projectId(m.getProjectId())
                .title(m.getTitle())
                .description(m.getDescription())
                .dueDate(m.getDueDate())
                .status(m.getStatus())
                .completedAt(m.getCompletedAt())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
