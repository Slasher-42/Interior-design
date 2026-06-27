package com.ced.Reporting.Analytics.Service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProjectManagerDashboardResponse(
        long totalProjects,
        long activeProjects,
        long completedProjects,
        List<ProjectSummary> projects,
        long totalTasks,
        long completedTasks,
        long pendingTasks
) {
}
