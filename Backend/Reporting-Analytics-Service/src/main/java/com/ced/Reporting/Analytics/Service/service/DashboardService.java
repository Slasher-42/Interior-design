package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.ProjectRecord;
import com.ced.Reporting.Analytics.Service.domain.ProjectRecordStatus;
import com.ced.Reporting.Analytics.Service.domain.QuotationRecord;
import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecord;
import com.ced.Reporting.Analytics.Service.domain.TaskRecord;
import com.ced.Reporting.Analytics.Service.domain.TaskRecordStatus;
import com.ced.Reporting.Analytics.Service.dto.AdminDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.ClientDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.DesignerDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.FeedbackSummaryResponse;
import com.ced.Reporting.Analytics.Service.dto.ProjectManagerDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.ProjectSummary;
import com.ced.Reporting.Analytics.Service.dto.QuotationSummary;
import com.ced.Reporting.Analytics.Service.dto.SalesDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.ServiceRequestSummary;
import com.ced.Reporting.Analytics.Service.dto.TaskSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserAnalyticsService userAnalyticsService;
    private final ClientAnalyticsService clientAnalyticsService;
    private final ServiceRequestAnalyticsService serviceRequestAnalyticsService;
    private final QuotationAnalyticsService quotationAnalyticsService;
    private final ProjectAnalyticsService projectAnalyticsService;
    private final ProcurementAnalyticsService procurementAnalyticsService;
    private final FeedbackAnalyticsService feedbackAnalyticsService;

    public AdminDashboardResponse adminDashboard(String authorizationHeader) {
        return AdminDashboardResponse.builder()
                .totalUsers(userAnalyticsService.totalUsers())
                .userSignupTrend(userAnalyticsService.signupTrend())
                .totalClients(clientAnalyticsService.totalClients())
                .clientGrowthTrend(clientAnalyticsService.growthTrend())
                .clientSegmentation(clientAnalyticsService.segmentation(authorizationHeader))
                .totalServiceRequests(serviceRequestAnalyticsService.totalRequests())
                .requestsByCategory(serviceRequestAnalyticsService.byCategory())
                .requestsByPriority(serviceRequestAnalyticsService.byPriority())
                .pendingQuotationValue(quotationAnalyticsService.pendingValue())
                .convertedQuotationValue(quotationAnalyticsService.convertedValue())
                .activeProjects(projectAnalyticsService.activeProjectsCount())
                .completedProjects(projectAnalyticsService.completedProjectsCount())
                .averageProjectDurationDays(projectAnalyticsService.averageProjectDurationDays())
                .totalApprovedBudget(projectAnalyticsService.totalApprovedBudget())
                .totalFinalCost(projectAnalyticsService.totalFinalCost())
                .taskOnTrackPercentage(projectAnalyticsService.taskOnTrackPercentage())
                .totalProcurementSpend(procurementAnalyticsService.totalSpend())
                .procurementSpendByProject(procurementAnalyticsService.spendByProject())
                .customerSatisfactionAverage(feedbackAnalyticsService.averageRating())
                .lowRatedFeedbackCount(feedbackAnalyticsService.lowRatedCount())
                .lowRatedFeedback(feedbackAnalyticsService.lowRatedFeedback().stream()
                        .map(FeedbackSummaryResponse::from).toList())
                .revenueTrend(quotationAnalyticsService.revenueTrend())
                .projectCompletionTrend(projectAnalyticsService.completionTrend())
                .taskThroughputPerTeamMember(projectAnalyticsService.taskThroughputPerTeamMember())
                .procurementCostTrend(procurementAnalyticsService.costTrend())
                .build();
    }

    public ProjectManagerDashboardResponse projectManagerDashboard(UUID managerId) {
        List<ProjectRecord> projects = projectAnalyticsService.projectsByManager(managerId);
        List<TaskRecord> tasks = projects.stream()
                .flatMap(p -> projectAnalyticsService.tasksByProject(p.getId()).stream())
                .toList();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskRecordStatus.COMPLETED).count();

        return ProjectManagerDashboardResponse.builder()
                .totalProjects(projects.size())
                .activeProjects(projects.stream().filter(p -> p.getStatus() == ProjectRecordStatus.ACTIVE).count())
                .completedProjects(projects.stream().filter(p -> p.getStatus() == ProjectRecordStatus.COMPLETED).count())
                .projects(projects.stream().map(ProjectSummary::from).toList())
                .totalTasks(tasks.size())
                .completedTasks(completedTasks)
                .pendingTasks(tasks.size() - completedTasks)
                .build();
    }

    public DesignerDashboardResponse designerDashboard(UUID designerId) {
        List<TaskRecord> tasks = projectAnalyticsService.tasksByAssignee(designerId);
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskRecordStatus.COMPLETED).count();

        return DesignerDashboardResponse.builder()
                .totalTasks(tasks.size())
                .completedTasks(completed)
                .pendingTasks(tasks.size() - completed)
                .tasks(tasks.stream().map(TaskSummary::from).toList())
                .build();
    }

    public ClientDashboardResponse clientDashboard(UUID clientId) {
        List<ProjectSummary> projects = projectAnalyticsService.projectsByClient(clientId).stream()
                .map(ProjectSummary::from).toList();
        List<FeedbackSummaryResponse> feedback = feedbackAnalyticsService.byClient(clientId).stream()
                .map(FeedbackSummaryResponse::from).toList();
        return new ClientDashboardResponse(projects, feedback);
    }

    public SalesDashboardResponse salesDashboard() {
        List<ServiceRequestRecord> pending = serviceRequestAnalyticsService.pending();
        List<QuotationRecord> open = quotationAnalyticsService.openQuotations();
        BigDecimal openValue = open.stream().map(QuotationRecord::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesDashboardResponse.builder()
                .pendingServiceRequestsCount(pending.size())
                .pendingServiceRequests(pending.stream().map(ServiceRequestSummary::from).toList())
                .openQuotationsCount(open.size())
                .openQuotationsValue(openValue)
                .openQuotations(open.stream().map(QuotationSummary::from).toList())
                .build();
    }
}
