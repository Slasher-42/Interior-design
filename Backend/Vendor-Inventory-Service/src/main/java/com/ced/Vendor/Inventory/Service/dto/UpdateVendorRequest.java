package com.ced.Vendor.Inventory.Service.dto;

import java.util.List;

public record UpdateVendorRequest(
        String name,
        String contactName,
        String email,
        String phone,
        List<String> suppliedMaterials,
        Boolean active
) {
}
