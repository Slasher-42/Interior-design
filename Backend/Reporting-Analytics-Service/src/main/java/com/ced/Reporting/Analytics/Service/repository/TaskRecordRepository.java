package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.TaskRecord;
import com.ced.Reporting.Analytics.Service.domain.TaskRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRecordRepository extends JpaRepository<TaskRecord, UUID> {

    List<TaskRecord> findByStatus(TaskRecordStatus status);

    List<TaskRecord> findByProjectId(UUID projectId);

    List<TaskRecord> findByAssignedUserId(UUID assignedUserId);
}
