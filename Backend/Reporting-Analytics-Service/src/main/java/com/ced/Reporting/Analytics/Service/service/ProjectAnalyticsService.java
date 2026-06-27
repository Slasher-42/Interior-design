package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.Priority;
import com.ced.Reporting.Analytics.Service.domain.ProjectRecord;
import com.ced.Reporting.Analytics.Service.domain.ProjectRecordStatus;
import com.ced.Reporting.Analytics.Service.domain.TaskRecord;
import com.ced.Reporting.Analytics.Service.domain.TaskRecordStatus;
import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;
import com.ced.Reporting.Analytics.Service.repository.ProjectRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.TaskRecordRepository;
import com.ced.Reporting.Analytics.Service.util.TimeSeriesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectAnalyticsService {

    private final ProjectRecordRepository projectRecordRepository;
    private final TaskRecordRepository taskRecordRepository;

    @Transactional
    public void recordProjectCreated(UUID projectId, UUID clientId, UUID projectManagerId,
                                      BigDecimal approvedBudget, java.time.Instant createdAt) {
        projectRecordRepository.save(ProjectRecord.builder()
                .id(projectId)
                .clientId(clientId)
                .projectManagerId(projectManagerId)
                .approvedBudget(approvedBudget)
                .createdAt(createdAt)
                .build());
    }

    @Transactional
    public void recordProjectCompleted(UUID projectId, BigDecimal finalCost, java.time.Instant completedAt) {
        projectRecordRepository.findById(projectId).ifPresent(record -> {
            record.setStatus(ProjectRecordStatus.COMPLETED);
            record.setFinalCost(finalCost);
            record.setCompletedAt(completedAt);
            projectRecordRepository.save(record);
        });
    }

    @Transactional
    public void recordTaskAssigned(UUID taskId, UUID projectId, UUID assignedUserId, Priority priority, LocalDate deadline) {
        taskRecordRepository.save(TaskRecord.builder()
                .id(taskId)
                .projectId(projectId)
                .assignedUserId(assignedUserId)
                .priority(priority)
                .deadline(deadline)
                .build());
    }

    @Transactional
    public void recordTaskCompleted(UUID taskId, java.time.Instant completedAt) {
        taskRecordRepository.findById(taskId).ifPresent(record -> {
            record.setStatus(TaskRecordStatus.COMPLETED);
            record.setCompletedAt(completedAt);
            taskRecordRepository.save(record);
        });
    }

    public long activeProjectsCount() {
        return projectRecordRepository.countByStatus(ProjectRecordStatus.ACTIVE);
    }

    public long completedProjectsCount() {
        return projectRecordRepository.countByStatus(ProjectRecordStatus.COMPLETED);
    }

    public Double averageProjectDurationDays() {
        List<ProjectRecord> completed = projectRecordRepository.findByStatus(ProjectRecordStatus.COMPLETED);
        return completed.isEmpty() ? null : completed.stream()
                .mapToLong(p -> Duration.between(p.getCreatedAt(), p.getCompletedAt()).toDays())
                .average()
                .orElse(0.0);
    }

    public BigDecimal totalApprovedBudget() {
        return projectRecordRepository.findByStatus(ProjectRecordStatus.COMPLETED).stream()
                .map(ProjectRecord::getApprovedBudget)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalFinalCost() {
        return projectRecordRepository.findByStatus(ProjectRecordStatus.COMPLETED).stream()
                .map(ProjectRecord::getFinalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double taskOnTrackPercentage() {
        List<TaskRecord> withDeadline = taskRecordRepository.findByStatus(TaskRecordStatus.COMPLETED).stream()
                .filter(t -> t.getDeadline() != null && t.getCompletedAt() != null)
                .toList();
        if (withDeadline.isEmpty()) {
            return 0.0;
        }
        long onTrack = withDeadline.stream()
                .filter(t -> !t.getCompletedAt().atZone(ZoneOffset.UTC).toLocalDate().isAfter(t.getDeadline()))
                .count();
        return (onTrack * 100.0) / withDeadline.size();
    }

    public List<TimeSeriesPoint> completionTrend() {
        return TimeSeriesUtil.bucketCountByMonth(
                projectRecordRepository.findByStatus(ProjectRecordStatus.COMPLETED), ProjectRecord::getCompletedAt);
    }

    public Map<UUID, Long> taskThroughputPerTeamMember() {
        return taskRecordRepository.findByStatus(TaskRecordStatus.COMPLETED).stream()
                .collect(Collectors.groupingBy(TaskRecord::getAssignedUserId, Collectors.counting()));
    }

    public List<ProjectRecord> projectsByManager(UUID managerId) {
        return projectRecordRepository.findByProjectManagerId(managerId);
    }

    public List<ProjectRecord> projectsByClient(UUID clientId) {
        return projectRecordRepository.findByClientId(clientId);
    }

    public List<TaskRecord> tasksByProject(UUID projectId) {
        return taskRecordRepository.findByProjectId(projectId);
    }

    public List<TaskRecord> tasksByAssignee(UUID assignedUserId) {
        return taskRecordRepository.findByAssignedUserId(assignedUserId);
    }
}
