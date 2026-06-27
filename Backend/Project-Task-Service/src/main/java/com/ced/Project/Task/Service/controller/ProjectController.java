package com.ced.Project.Task.Service.controller;

import com.ced.Project.Task.Service.domain.ProjectStatus;
import com.ced.Project.Task.Service.domain.Role;
import com.ced.Project.Task.Service.dto.CompleteProjectRequest;
import com.ced.Project.Task.Service.dto.ProjectResponse;
import com.ced.Project.Task.Service.dto.ProjectSetupRequest;
import com.ced.Project.Task.Service.security.CurrentUser;
import com.ced.Project.Task.Service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> search(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID projectManagerId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID effectiveClientId = CurrentUser.role() == Role.CLIENT ? CurrentUser.id() : clientId;
        Page<ProjectResponse> result = projectService
                .search(effectiveClientId, projectManagerId, status, PageRequest.of(page, size))
                .map(ProjectResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable UUID id) {
        var project = projectService.getByIdForCaller(id, CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(ProjectResponse.from(project));
    }

    @PatchMapping("/{id}/setup")
    public ResponseEntity<ProjectResponse> setup(@PathVariable UUID id, @Valid @RequestBody ProjectSetupRequest request) {
        return ResponseEntity.ok(ProjectResponse.from(projectService.setup(id, request)));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProjectResponse> complete(@PathVariable UUID id,
                                                     @Valid @RequestBody CompleteProjectRequest request) {
        return ResponseEntity.ok(ProjectResponse.from(projectService.complete(id, request)));
    }
}
