package com.ced.Service.Request.Quotation.Service.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectQuotationRequest(
        @NotBlank String reason
) {
}
