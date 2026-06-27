package com.ced.Feedback.Communication.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Service-Request-Quotation-Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationCreatedEvent {
    private UUID quotationId;
    private UUID requestId;
    private UUID clientId;
    private BigDecimal totalAmount;
    private QuotationStatus status;
    private Instant createdAt;
}
