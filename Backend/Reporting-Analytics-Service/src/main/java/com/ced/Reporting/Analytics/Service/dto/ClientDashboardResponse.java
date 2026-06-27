package com.ced.Reporting.Analytics.Service.dto;

import java.util.List;

public record ClientDashboardResponse(
        List<ProjectSummary> projects,
        List<FeedbackSummaryResponse> feedbackHistory
) {
}
