package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.QuotationRecord;
import com.ced.Reporting.Analytics.Service.domain.QuotationRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuotationRecordRepository extends JpaRepository<QuotationRecord, UUID> {

    List<QuotationRecord> findByStatus(QuotationRecordStatus status);
}
