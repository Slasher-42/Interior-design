package com.ced.Vendor.Inventory.Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MaterialLineItemRequest(
        @NotBlank String materialName,
        @Positive int quantity
) {
}
