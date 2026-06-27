package com.ced.Document.Portfolio.Service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateFolderRequest(
        @NotBlank String name,
        UUID parentFolderId
) {
}
