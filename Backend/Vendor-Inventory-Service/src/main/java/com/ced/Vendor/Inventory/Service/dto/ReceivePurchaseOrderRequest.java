package com.ced.Vendor.Inventory.Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceivePurchaseOrderRequest(
        @NotNull @DecimalMin(value = "0", message = "actualCost cannot be negative") BigDecimal actualCost,
        LocalDate actualDeliveryDate,
        @NotNull Boolean accurate
) {
}
