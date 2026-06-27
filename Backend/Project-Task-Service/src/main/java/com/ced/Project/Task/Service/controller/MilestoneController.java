package com.ced.Project.Task.Service.controller;

import com.ced.Project.Task.Service.dto.CreateMilestoneRequest;
import com.ced.Project.Task.Service.dto.MilestoneResponse;
import com.ced.Project.Task.Service.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping("/projects/{projectId}/milestones")
    public ResponseEntity<MilestoneResponse> create(@PathVariable UUID projectId,
                                                     @Valid @RequestBody CreateMilestoneRequest request) {
        MilestoneResponse response = MilestoneResponse.from(milestoneService.create(projectId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/milestones")
    public ResponseEntity<List<MilestoneResponse>> listByProject(@PathVariable UUID projectId) {
        List<MilestoneResponse> result = milestoneService.listByProject(projectId).stream()
                .map(MilestoneResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/milestones/{id}")
    public ResponseEntity<MilestoneResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(MilestoneResponse.from(milestoneService.getById(id)));
    }
}
