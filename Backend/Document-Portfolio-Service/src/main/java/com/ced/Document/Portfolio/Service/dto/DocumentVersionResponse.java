package com.ced.Document.Portfolio.Service.dto;

import com.ced.Document.Portfolio.Service.domain.DocumentVersion;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DocumentVersionResponse(
        UUID id,
        UUID documentId,
        int versionNumber,
        String fileName,
        String contentType,
        long fileSize,
        UUID uploadedBy,
        Instant createdAt
) {
    public static DocumentVersionResponse from(DocumentVersion v) {
        return DocumentVersionResponse.builder()
                .id(v.getId())
                .documentId(v.getDocumentId())
                .versionNumber(v.getVersionNumber())
                .fileName(v.getFileName())
                .contentType(v.getContentType())
                .fileSize(v.getFileSize())
                .uploadedBy(v.getUploadedBy())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
