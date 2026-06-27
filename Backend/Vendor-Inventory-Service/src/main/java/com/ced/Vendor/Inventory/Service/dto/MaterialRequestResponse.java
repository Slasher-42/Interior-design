package com.ced.Vendor.Inventory.Service.dto;

import com.ced.Vendor.Inventory.Service.domain.MaterialLineItem;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequest;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequestStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record MaterialRequestResponse(
        UUID id,
        UUID projectId,
        UUID requestedBy,
        List<MaterialLineItem> items,
        MaterialRequestStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static MaterialRequestResponse from(MaterialRequest m) {
        return MaterialRequestResponse.builder()
                .id(m.getId())
                .projectId(m.getProjectId())
                .requestedBy(m.getRequestedBy())
                .items(m.getItems())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
