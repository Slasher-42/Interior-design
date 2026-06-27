package com.ced.Project.Task.Service.service;

import com.ced.Project.Task.Service.domain.Milestone;
import com.ced.Project.Task.Service.domain.MilestoneStatus;
import com.ced.Project.Task.Service.domain.TaskStatus;
import com.ced.Project.Task.Service.dto.CreateMilestoneRequest;
import com.ced.Project.Task.Service.exception.AppException;
import com.ced.Project.Task.Service.repository.MilestoneRepository;
import com.ced.Project.Task.Service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Transactional
    public Milestone create(UUID projectId, CreateMilestoneRequest request) {
        projectService.getById(projectId);

        Milestone milestone = Milestone.builder()
                .projectId(projectId)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .build();
        return milestoneRepository.save(milestone);
    }

    public Milestone getById(UUID id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new AppException("Milestone not found", HttpStatus.NOT_FOUND));
    }

    public List<Milestone> listByProject(UUID projectId) {
        return milestoneRepository.findByProjectId(projectId);
    }

    @Transactional
    void completeIfAllTasksDone(UUID milestoneId) {
        boolean hasIncompleteTask = taskRepository.existsByMilestoneIdAndStatusNot(milestoneId, TaskStatus.COMPLETED);
        if (hasIncompleteTask) {
            return;
        }
        Milestone milestone = getById(milestoneId);
        if (milestone.getStatus() != MilestoneStatus.COMPLETED) {
            milestone.setStatus(MilestoneStatus.COMPLETED);
            milestone.setCompletedAt(Instant.now());
            milestoneRepository.save(milestone);
        }
    }
}
