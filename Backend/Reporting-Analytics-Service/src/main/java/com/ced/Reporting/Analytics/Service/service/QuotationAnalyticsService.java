package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.QuotationRecord;
import com.ced.Reporting.Analytics.Service.domain.QuotationRecordStatus;
import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;
import com.ced.Reporting.Analytics.Service.repository.QuotationRecordRepository;
import com.ced.Reporting.Analytics.Service.util.TimeSeriesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotationAnalyticsService {

    private final QuotationRecordRepository quotationRecordRepository;

    @Transactional
    public void recordCreated(UUID quotationId, UUID requestId, UUID clientId, BigDecimal totalAmount, Instant createdAt) {
        quotationRecordRepository.save(QuotationRecord.builder()
                .id(quotationId)
                .requestId(requestId)
                .clientId(clientId)
                .totalAmount(totalAmount)
                .createdAt(createdAt)
                .build());
    }

    @Transactional
    public void recordApproved(UUID quotationId, Instant approvedAt) {
        quotationRecordRepository.findById(quotationId).ifPresent(record -> {
            record.setStatus(QuotationRecordStatus.APPROVED);
            record.setApprovedAt(approvedAt);
            quotationRecordRepository.save(record);
        });
    }

    public BigDecimal pendingValue() {
        return sumByStatus(QuotationRecordStatus.PENDING_APPROVAL);
    }

    public BigDecimal convertedValue() {
        return sumByStatus(QuotationRecordStatus.APPROVED);
    }

    public List<QuotationRecord> openQuotations() {
        return quotationRecordRepository.findByStatus(QuotationRecordStatus.PENDING_APPROVAL);
    }

    public List<TimeSeriesPoint> revenueTrend() {
        List<QuotationRecord> approved = quotationRecordRepository.findByStatus(QuotationRecordStatus.APPROVED);
        return TimeSeriesUtil.bucketSumByMonth(approved, QuotationRecord::getApprovedAt, QuotationRecord::getTotalAmount);
    }

    private BigDecimal sumByStatus(QuotationRecordStatus status) {
        return quotationRecordRepository.findByStatus(status).stream()
                .map(QuotationRecord::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
