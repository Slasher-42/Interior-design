package com.ced.Service.Request.Quotation.Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateQuotationRequest(
        @NotNull @DecimalMin(value = "0", message = "materialCost cannot be negative") BigDecimal materialCost,
        @NotNull @DecimalMin(value = "0", message = "laborCost cannot be negative") BigDecimal laborCost,
        @NotNull @DecimalMin(value = "0", message = "additionalCharges cannot be negative") BigDecimal additionalCharges
) {
}
