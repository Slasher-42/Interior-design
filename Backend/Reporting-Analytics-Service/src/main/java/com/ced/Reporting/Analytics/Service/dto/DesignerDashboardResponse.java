package com.ced.Reporting.Analytics.Service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DesignerDashboardResponse(
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        List<TaskSummary> tasks
) {
}
