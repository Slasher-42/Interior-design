package com.ced.Document.Portfolio.Service.dto;

import com.ced.Document.Portfolio.Service.domain.Folder;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FolderResponse(
        UUID id,
        UUID projectId,
        String name,
        UUID parentFolderId,
        Instant createdAt
) {
    public static FolderResponse from(Folder f) {
        return FolderResponse.builder()
                .id(f.getId())
                .projectId(f.getProjectId())
                .name(f.getName())
                .parentFolderId(f.getParentFolderId())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
