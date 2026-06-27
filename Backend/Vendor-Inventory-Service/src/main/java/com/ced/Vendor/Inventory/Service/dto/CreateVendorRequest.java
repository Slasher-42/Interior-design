package com.ced.Vendor.Inventory.Service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateVendorRequest(
        @NotBlank String name,
        String contactName,
        String email,
        String phone,
        List<String> suppliedMaterials
) {
}
