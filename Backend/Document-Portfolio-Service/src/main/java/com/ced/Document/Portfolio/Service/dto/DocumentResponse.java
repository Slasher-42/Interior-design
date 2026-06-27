package com.ced.Document.Portfolio.Service.dto;

import com.ced.Document.Portfolio.Service.domain.Document;
import com.ced.Document.Portfolio.Service.domain.DocumentStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DocumentResponse(
        UUID id,
        UUID projectId,
        UUID folderId,
        String title,
        String description,
        UUID uploadedBy,
        DocumentStatus status,
        UUID approvedBy,
        Instant approvedAt,
        String rejectionReason,
        int currentVersion,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentResponse from(Document d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .projectId(d.getProjectId())
                .folderId(d.getFolderId())
                .title(d.getTitle())
                .description(d.getDescription())
                .uploadedBy(d.getUploadedBy())
                .status(d.getStatus())
                .approvedBy(d.getApprovedBy())
                .approvedAt(d.getApprovedAt())
                .rejectionReason(d.getRejectionReason())
                .currentVersion(d.getCurrentVersion())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
