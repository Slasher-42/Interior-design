package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.PurchaseOrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderRecordRepository extends JpaRepository<PurchaseOrderRecord, UUID> {
}
