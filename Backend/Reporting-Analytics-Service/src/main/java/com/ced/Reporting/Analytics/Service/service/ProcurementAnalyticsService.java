package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.PurchaseOrderRecord;
import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;
import com.ced.Reporting.Analytics.Service.repository.PurchaseOrderRecordRepository;
import com.ced.Reporting.Analytics.Service.util.TimeSeriesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcurementAnalyticsService {

    private final PurchaseOrderRecordRepository purchaseOrderRecordRepository;

    @Transactional
    public void recordCreated(UUID orderId, UUID projectId, UUID vendorId, BigDecimal estimatedCost, Instant createdAt) {
        purchaseOrderRecordRepository.save(PurchaseOrderRecord.builder()
                .id(orderId)
                .projectId(projectId)
                .vendorId(vendorId)
                .estimatedCost(estimatedCost)
                .createdAt(createdAt)
                .build());
    }

    public BigDecimal totalSpend() {
        return purchaseOrderRecordRepository.findAll().stream()
                .map(PurchaseOrderRecord::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<UUID, BigDecimal> spendByProject() {
        return purchaseOrderRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(PurchaseOrderRecord::getProjectId,
                        Collectors.reducing(BigDecimal.ZERO, PurchaseOrderRecord::getEstimatedCost, BigDecimal::add)));
    }

    public List<TimeSeriesPoint> costTrend() {
        return TimeSeriesUtil.bucketSumByMonth(purchaseOrderRecordRepository.findAll(),
                PurchaseOrderRecord::getCreatedAt, PurchaseOrderRecord::getEstimatedCost);
    }
}
