package com.ced.Project.Task.Service.repository;

import com.ced.Project.Task.Service.domain.Milestone;
import com.ced.Project.Task.Service.domain.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {

    List<Milestone> findByProjectId(UUID projectId);

    boolean existsByProjectIdAndStatusNot(UUID projectId, MilestoneStatus status);
}
