package com.ced.Reporting.Analytics.Service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SalesDashboardResponse(
        long pendingServiceRequestsCount,
        List<ServiceRequestSummary> pendingServiceRequests,
        long openQuotationsCount,
        BigDecimal openQuotationsValue,
        List<QuotationSummary> openQuotations
) {
}
