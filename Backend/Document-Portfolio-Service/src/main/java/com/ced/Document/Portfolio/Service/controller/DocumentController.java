package com.ced.Document.Portfolio.Service.controller;

import com.ced.Document.Portfolio.Service.domain.DocumentStatus;
import com.ced.Document.Portfolio.Service.dto.DocumentResponse;
import com.ced.Document.Portfolio.Service.dto.DocumentVersionResponse;
import com.ced.Document.Portfolio.Service.dto.RejectDocumentRequest;
import com.ced.Document.Portfolio.Service.security.CurrentUser;
import com.ced.Document.Portfolio.Service.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/projects/{projectId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @PathVariable UUID projectId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) UUID folderId,
            @RequestParam("file") MultipartFile file) {
        var document = documentService.upload(projectId, CurrentUser.id(), title, description, folderId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentResponse.from(document));
    }

    @PostMapping(value = "/documents/{id}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadNewVersion(@PathVariable UUID id,
                                                              @RequestParam("file") MultipartFile file) {
        var document = documentService.addVersion(id, CurrentUser.id(), CurrentUser.role(), file);
        return ResponseEntity.ok(DocumentResponse.from(document));
    }

    @GetMapping("/projects/{projectId}/documents")
    public ResponseEntity<Page<DocumentResponse>> search(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID folderId,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) UUID uploadedBy,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DocumentResponse> result = documentService
                .search(projectId, folderId, status, uploadedBy, query, CurrentUser.role(), PageRequest.of(page, size))
                .map(DocumentResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectId}/gallery")
    public ResponseEntity<Page<DocumentResponse>> gallery(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DocumentResponse> result = documentService
                .search(projectId, null, DocumentStatus.APPROVED, null, null, CurrentUser.role(), PageRequest.of(page, size))
                .map(DocumentResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(DocumentResponse.from(documentService.getByIdForCaller(id, CurrentUser.role())));
    }

    @GetMapping("/documents/{id}/versions")
    public ResponseEntity<List<DocumentVersionResponse>> listVersions(@PathVariable UUID id) {
        List<DocumentVersionResponse> result = documentService.listVersions(id, CurrentUser.role()).stream()
                .map(DocumentVersionResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/documents/{id}/file")
    public ResponseEntity<byte[]> downloadLatest(@PathVariable UUID id) {
        return fileResponse(documentService.getFile(id, null, CurrentUser.role()));
    }

    @GetMapping("/documents/{id}/versions/{versionNumber}/file")
    public ResponseEntity<byte[]> downloadVersion(@PathVariable UUID id, @PathVariable int versionNumber) {
        return fileResponse(documentService.getFile(id, versionNumber, CurrentUser.role()));
    }

    @PatchMapping("/documents/{id}/review")
    public ResponseEntity<DocumentResponse> markUnderReview(@PathVariable UUID id) {
        return ResponseEntity.ok(DocumentResponse.from(documentService.markUnderReview(id)));
    }

    @PatchMapping("/documents/{id}/approve")
    public ResponseEntity<DocumentResponse> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(DocumentResponse.from(documentService.approve(id, CurrentUser.id())));
    }

    @PatchMapping("/documents/{id}/reject")
    public ResponseEntity<DocumentResponse> reject(@PathVariable UUID id,
                                                    @Valid @RequestBody RejectDocumentRequest request) {
        return ResponseEntity.ok(DocumentResponse.from(documentService.reject(id, request.reason())));
    }

    private ResponseEntity<byte[]> fileResponse(DocumentService.StoredFile file) {
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(file.contentType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.fileName() + "\"")
                .body(file.bytes());
    }
}
