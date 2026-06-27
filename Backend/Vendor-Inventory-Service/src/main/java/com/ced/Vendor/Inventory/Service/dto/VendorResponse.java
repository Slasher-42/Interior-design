package com.ced.Vendor.Inventory.Service.dto;

import com.ced.Vendor.Inventory.Service.domain.Vendor;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record VendorResponse(
        UUID id,
        String name,
        String contactName,
        String email,
        String phone,
        List<String> suppliedMaterials,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static VendorResponse from(Vendor v) {
        return VendorResponse.builder()
                .id(v.getId())
                .name(v.getName())
                .contactName(v.getContactName())
                .email(v.getEmail())
                .phone(v.getPhone())
                .suppliedMaterials(v.getSuppliedMaterials())
                .active(v.isActive())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
