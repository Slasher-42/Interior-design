package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecord;
import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRequestRecordRepository extends JpaRepository<ServiceRequestRecord, UUID> {

    List<ServiceRequestRecord> findByStatus(ServiceRequestRecordStatus status);

    long countByStatus(ServiceRequestRecordStatus status);
}
