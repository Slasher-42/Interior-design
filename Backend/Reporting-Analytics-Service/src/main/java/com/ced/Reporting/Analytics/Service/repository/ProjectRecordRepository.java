package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.ProjectRecord;
import com.ced.Reporting.Analytics.Service.domain.ProjectRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRecordRepository extends JpaRepository<ProjectRecord, UUID> {

    long countByStatus(ProjectRecordStatus status);

    List<ProjectRecord> findByStatus(ProjectRecordStatus status);

    List<ProjectRecord> findByProjectManagerId(UUID projectManagerId);

    List<ProjectRecord> findByClientId(UUID clientId);
}
