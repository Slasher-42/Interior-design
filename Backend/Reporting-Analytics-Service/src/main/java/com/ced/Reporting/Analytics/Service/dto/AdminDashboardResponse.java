package com.ced.Reporting.Analytics.Service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record AdminDashboardResponse(
        long totalUsers,
        List<TimeSeriesPoint> userSignupTrend,
        long totalClients,
        List<TimeSeriesPoint> clientGrowthTrend,
        List<ClientSegmentSummaryResponse> clientSegmentation,
        long totalServiceRequests,
        Map<String, Long> requestsByCategory,
        Map<String, Long> requestsByPriority,
        BigDecimal pendingQuotationValue,
        BigDecimal convertedQuotationValue,
        long activeProjects,
        long completedProjects,
        Double averageProjectDurationDays,
        BigDecimal totalApprovedBudget,
        BigDecimal totalFinalCost,
        double taskOnTrackPercentage,
        BigDecimal totalProcurementSpend,
        Map<UUID, BigDecimal> procurementSpendByProject,
        Double customerSatisfactionAverage,
        long lowRatedFeedbackCount,
        List<FeedbackSummaryResponse> lowRatedFeedback,
        List<TimeSeriesPoint> revenueTrend,
        List<TimeSeriesPoint> projectCompletionTrend,
        Map<UUID, Long> taskThroughputPerTeamMember,
        List<TimeSeriesPoint> procurementCostTrend
) {
}
