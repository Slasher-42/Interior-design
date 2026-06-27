package com.ced.Project.Task.Service.service;

import com.ced.Project.Task.Service.domain.MilestoneStatus;
import com.ced.Project.Task.Service.domain.Project;
import com.ced.Project.Task.Service.domain.ProjectStatus;
import com.ced.Project.Task.Service.domain.Role;
import com.ced.Project.Task.Service.dto.CompleteProjectRequest;
import com.ced.Project.Task.Service.dto.ProjectSetupRequest;
import com.ced.Project.Task.Service.event.KafkaEventPublisher;
import com.ced.Project.Task.Service.event.ProjectCompletedEvent;
import com.ced.Project.Task.Service.event.ProjectCreatedEvent;
import com.ced.Project.Task.Service.event.PurchaseOrderCreatedEvent;
import com.ced.Project.Task.Service.event.QuotationApprovedEvent;
import com.ced.Project.Task.Service.exception.AppException;
import com.ced.Project.Task.Service.repository.MilestoneRepository;
import com.ced.Project.Task.Service.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public void handleQuotationApproved(QuotationApprovedEvent event) {
        Project project = Project.builder()
                .clientId(event.getClientId())
                .requestId(event.getRequestId())
                .quotationId(event.getQuotationId())
                .approvedBudget(event.getTotalAmount())
                .build();
        project = projectRepository.save(project);

        kafkaEventPublisher.publishProjectCreated(ProjectCreatedEvent.builder()
                .projectId(project.getId())
                .clientId(project.getClientId())
                .requestId(project.getRequestId())
                .quotationId(project.getQuotationId())
                .projectManagerId(project.getProjectManagerId())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .approvedBudget(project.getApprovedBudget())
                .createdAt(project.getCreatedAt())
                .build());
    }

    @Transactional
    public void handlePurchaseOrderCreated(PurchaseOrderCreatedEvent event) {
        Project project = getById(event.getProjectId());
        project.setMaterialCost(project.getMaterialCost().add(event.getEstimatedCost()));
        project.setBudgetOverrun(project.getMaterialCost().compareTo(project.getApprovedBudget()) > 0);
        projectRepository.save(project);
    }

    @Transactional
    public Project setup(UUID id, ProjectSetupRequest request) {
        Project project = getById(id);
        if (project.getStatus() != ProjectStatus.PLANNING) {
            throw new AppException("Only projects in planning can be set up", HttpStatus.CONFLICT);
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new AppException("endDate cannot be before startDate", HttpStatus.BAD_REQUEST);
        }
        project.setProjectManagerId(request.projectManagerId());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setStatus(ProjectStatus.ACTIVE);
        return projectRepository.save(project);
    }

    @Transactional
    public Project complete(UUID id, CompleteProjectRequest request) {
        Project project = getById(id);
        if (project.getStatus() != ProjectStatus.ACTIVE) {
            throw new AppException("Only active projects can be completed", HttpStatus.CONFLICT);
        }
        if (milestoneRepository.existsByProjectIdAndStatusNot(id, MilestoneStatus.COMPLETED)) {
            throw new AppException("All milestones must be completed first", HttpStatus.CONFLICT);
        }

        project.setStatus(ProjectStatus.COMPLETED);
        project.setFinalCost(request.finalCost() != null ? request.finalCost() : project.getMaterialCost());
        project.setCompletedAt(Instant.now());
        project = projectRepository.save(project);

        kafkaEventPublisher.publishProjectCompleted(ProjectCompletedEvent.builder()
                .projectId(project.getId())
                .clientId(project.getClientId())
                .completedAt(project.getCompletedAt())
                .finalCost(project.getFinalCost())
                .build());

        return project;
    }

    public Project getById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));
    }

    public Project getByIdForCaller(UUID id, UUID callerId, Role callerRole) {
        Project project = getById(id);
        if (callerRole == Role.CLIENT && !project.getClientId().equals(callerId)) {
            throw new AppException("Project not found", HttpStatus.NOT_FOUND);
        }
        return project;
    }

    public Page<Project> search(UUID clientId, UUID projectManagerId, ProjectStatus status, Pageable pageable) {
        Specification<Project> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (clientId != null) {
                predicates.add(cb.equal(root.get("clientId"), clientId));
            }
            if (projectManagerId != null) {
                predicates.add(cb.equal(root.get("projectManagerId"), projectManagerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return projectRepository.findAll(spec, pageable);
    }
}
