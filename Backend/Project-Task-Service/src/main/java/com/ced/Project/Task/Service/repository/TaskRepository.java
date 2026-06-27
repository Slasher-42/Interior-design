package com.ced.Project.Task.Service.repository;

import com.ced.Project.Task.Service.domain.Task;
import com.ced.Project.Task.Service.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findByMilestoneId(UUID milestoneId);

    boolean existsByMilestoneIdAndStatusNot(UUID milestoneId, TaskStatus status);
}
