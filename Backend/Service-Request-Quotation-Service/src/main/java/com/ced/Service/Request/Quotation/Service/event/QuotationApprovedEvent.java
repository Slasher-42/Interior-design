package com.ced.Service.Request.Quotation.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationApprovedEvent {
    private UUID quotationId;
    private UUID requestId;
    private UUID clientId;
    private BigDecimal totalAmount;
    private Instant approvedAt;
}
