package com.ced.Reporting.Analytics.Service.dto;

import com.ced.Reporting.Analytics.Service.domain.QuotationRecord;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record QuotationSummary(
        UUID id,
        UUID clientId,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
    public static QuotationSummary from(QuotationRecord q) {
        return QuotationSummary.builder()
                .id(q.getId())
                .clientId(q.getClientId())
                .totalAmount(q.getTotalAmount())
                .status(q.getStatus().name())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
