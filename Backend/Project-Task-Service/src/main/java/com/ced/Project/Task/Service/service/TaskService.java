package com.ced.Project.Task.Service.service;

import com.ced.Project.Task.Service.domain.Milestone;
import com.ced.Project.Task.Service.domain.Role;
import com.ced.Project.Task.Service.domain.Task;
import com.ced.Project.Task.Service.domain.TaskStatus;
import com.ced.Project.Task.Service.dto.CreateTaskRequest;
import com.ced.Project.Task.Service.event.KafkaEventPublisher;
import com.ced.Project.Task.Service.event.TaskAssignedEvent;
import com.ced.Project.Task.Service.event.TaskCompletedEvent;
import com.ced.Project.Task.Service.exception.AppException;
import com.ced.Project.Task.Service.repository.TaskRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MilestoneService milestoneService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public Task create(UUID milestoneId, CreateTaskRequest request) {
        Milestone milestone = milestoneService.getById(milestoneId);

        Task task = Task.builder()
                .projectId(milestone.getProjectId())
                .milestoneId(milestoneId)
                .title(request.title())
                .description(request.description())
                .assignedUserId(request.assignedUserId())
                .priority(request.priority())
                .deadline(request.deadline())
                .build();
        task = taskRepository.save(task);

        kafkaEventPublisher.publishTaskAssigned(TaskAssignedEvent.builder()
                .taskId(task.getId())
                .projectId(task.getProjectId())
                .assignedUserId(task.getAssignedUserId())
                .title(task.getTitle())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .build());

        return task;
    }

    @Transactional
    public Task complete(UUID id, UUID callerId, Role callerRole) {
        Task task = getById(id);
        if (callerRole != Role.ADMIN && callerRole != Role.PROJECT_MANAGER
                && !task.getAssignedUserId().equals(callerId)) {
            throw new AppException("Only the assigned user can complete this task", HttpStatus.FORBIDDEN);
        }
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new AppException("Task is already completed", HttpStatus.CONFLICT);
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());
        task = taskRepository.save(task);

        kafkaEventPublisher.publishTaskCompleted(TaskCompletedEvent.builder()
                .taskId(task.getId())
                .projectId(task.getProjectId())
                .milestoneId(task.getMilestoneId())
                .assignedUserId(task.getAssignedUserId())
                .completedAt(task.getCompletedAt())
                .build());

        milestoneService.completeIfAllTasksDone(task.getMilestoneId());

        return task;
    }

    public Task getById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AppException("Task not found", HttpStatus.NOT_FOUND));
    }

    public List<Task> listByMilestone(UUID milestoneId) {
        return taskRepository.findByMilestoneId(milestoneId);
    }

    public Page<Task> search(UUID projectId, UUID assignedUserId, TaskStatus status, Pageable pageable) {
        Specification<Task> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (projectId != null) {
                predicates.add(cb.equal(root.get("projectId"), projectId));
            }
            if (assignedUserId != null) {
                predicates.add(cb.equal(root.get("assignedUserId"), assignedUserId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return taskRepository.findAll(spec, pageable);
    }
}
