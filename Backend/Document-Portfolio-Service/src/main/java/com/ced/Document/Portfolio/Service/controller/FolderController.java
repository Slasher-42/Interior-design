package com.ced.Document.Portfolio.Service.controller;

import com.ced.Document.Portfolio.Service.dto.CreateFolderRequest;
import com.ced.Document.Portfolio.Service.dto.FolderResponse;
import com.ced.Document.Portfolio.Service.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/projects/{projectId}/folders")
    public ResponseEntity<FolderResponse> create(@PathVariable UUID projectId,
                                                  @Valid @RequestBody CreateFolderRequest request) {
        FolderResponse response = FolderResponse.from(folderService.create(projectId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/folders")
    public ResponseEntity<List<FolderResponse>> listByProject(@PathVariable UUID projectId) {
        List<FolderResponse> result = folderService.listByProject(projectId).stream()
                .map(FolderResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
