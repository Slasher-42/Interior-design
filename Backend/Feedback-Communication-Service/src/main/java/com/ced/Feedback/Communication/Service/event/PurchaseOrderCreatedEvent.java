package com.ced.Feedback.Communication.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Local mirror of the event published by Vendor & Inventory Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreatedEvent {
    private UUID orderId;
    private UUID projectId;
    private UUID vendorId;
    private List<String> materials;
    private BigDecimal estimatedCost;
    private Instant createdAt;
}
