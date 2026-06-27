package com.ced.Vendor.Inventory.Service.dto;

import com.ced.Vendor.Inventory.Service.domain.InventoryItem;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record InventoryItemResponse(
        UUID id,
        UUID projectId,
        String materialName,
        int quantityOnHand,
        Instant updatedAt
) {
    public static InventoryItemResponse from(InventoryItem i) {
        return InventoryItemResponse.builder()
                .id(i.getId())
                .projectId(i.getProjectId())
                .materialName(i.getMaterialName())
                .quantityOnHand(i.getQuantityOnHand())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
