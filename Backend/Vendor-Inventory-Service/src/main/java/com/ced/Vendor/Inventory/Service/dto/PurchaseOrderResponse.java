package com.ced.Vendor.Inventory.Service.dto;

import com.ced.Vendor.Inventory.Service.domain.MaterialLineItem;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrder;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record PurchaseOrderResponse(
        UUID id,
        UUID materialRequestId,
        UUID projectId,
        UUID vendorId,
        List<MaterialLineItem> items,
        BigDecimal estimatedCost,
        BigDecimal actualCost,
        PurchaseOrderStatus status,
        LocalDate expectedDeliveryDate,
        LocalDate actualDeliveryDate,
        Boolean accurate,
        Instant createdAt,
        Instant updatedAt
) {
    public static PurchaseOrderResponse from(PurchaseOrder o) {
        return PurchaseOrderResponse.builder()
                .id(o.getId())
                .materialRequestId(o.getMaterialRequestId())
                .projectId(o.getProjectId())
                .vendorId(o.getVendorId())
                .items(o.getItems())
                .estimatedCost(o.getEstimatedCost())
                .actualCost(o.getActualCost())
                .status(o.getStatus())
                .expectedDeliveryDate(o.getExpectedDeliveryDate())
                .actualDeliveryDate(o.getActualDeliveryDate())
                .accurate(o.getAccurate())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
