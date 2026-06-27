package com.ced.Vendor.Inventory.Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePurchaseOrderRequest(
        @NotNull UUID vendorId,
        @NotNull @DecimalMin(value = "0", message = "estimatedCost cannot be negative") BigDecimal estimatedCost,
        LocalDate expectedDeliveryDate
) {
}
