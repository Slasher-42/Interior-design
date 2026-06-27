package com.ced.Service.Request.Quotation.Service.dto;

import com.ced.Service.Request.Quotation.Service.domain.Quotation;
import com.ced.Service.Request.Quotation.Service.domain.QuotationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record QuotationResponse(
        UUID id,
        UUID requestId,
        UUID clientId,
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal additionalCharges,
        BigDecimal totalAmount,
        QuotationStatus status,
        Instant approvedAt,
        Instant rejectedAt,
        String rejectionReason,
        Instant createdAt
) {
    public static QuotationResponse from(Quotation q) {
        return QuotationResponse.builder()
                .id(q.getId())
                .requestId(q.getRequestId())
                .clientId(q.getClientId())
                .materialCost(q.getMaterialCost())
                .laborCost(q.getLaborCost())
                .additionalCharges(q.getAdditionalCharges())
                .totalAmount(q.getTotalAmount())
                .status(q.getStatus())
                .approvedAt(q.getApprovedAt())
                .rejectedAt(q.getRejectedAt())
                .rejectionReason(q.getRejectionReason())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
