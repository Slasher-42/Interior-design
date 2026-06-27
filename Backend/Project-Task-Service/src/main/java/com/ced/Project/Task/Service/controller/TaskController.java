package com.ced.Project.Task.Service.controller;

import com.ced.Project.Task.Service.domain.Role;
import com.ced.Project.Task.Service.domain.TaskStatus;
import com.ced.Project.Task.Service.dto.CreateTaskRequest;
import com.ced.Project.Task.Service.dto.TaskResponse;
import com.ced.Project.Task.Service.security.CurrentUser;
import com.ced.Project.Task.Service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/milestones/{milestoneId}/tasks")
    public ResponseEntity<TaskResponse> create(@PathVariable UUID milestoneId,
                                               @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = TaskResponse.from(taskService.create(milestoneId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/milestones/{milestoneId}/tasks")
    public ResponseEntity<List<TaskResponse>> listByMilestone(@PathVariable UUID milestoneId) {
        List<TaskResponse> result = taskService.listByMilestone(milestoneId).stream()
                .map(TaskResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> search(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID assignedUserId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID effectiveAssignedUserId = CurrentUser.role() == Role.DESIGNER ? CurrentUser.id() : assignedUserId;
        Page<TaskResponse> result = taskService
                .search(projectId, effectiveAssignedUserId, status, PageRequest.of(page, size))
                .map(TaskResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(TaskResponse.from(taskService.getById(id)));
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskResponse> complete(@PathVariable UUID id) {
        TaskResponse response = TaskResponse.from(taskService.complete(id, CurrentUser.id(), CurrentUser.role()));
        return ResponseEntity.ok(response);
    }
}
