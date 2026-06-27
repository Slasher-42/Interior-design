package com.ced.Document.Portfolio.Service.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectDocumentRequest(
        @NotBlank String reason
) {
}
