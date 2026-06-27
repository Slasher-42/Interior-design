package com.ced.Vendor.Inventory.Service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record VendorPerformanceResponse(
        UUID vendorId,
        long totalOrders,
        long receivedOrders,
        double onTimeDeliveryRate,
        double accuracyRate,
        Double averageCostVariancePercent,
        Double averageDeliveryDelayDays
) {
}
