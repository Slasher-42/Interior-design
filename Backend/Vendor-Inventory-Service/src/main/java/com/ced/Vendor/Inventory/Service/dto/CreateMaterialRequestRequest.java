package com.ced.Vendor.Inventory.Service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateMaterialRequestRequest(
        @NotEmpty List<@Valid MaterialLineItemRequest> items
) {
}
