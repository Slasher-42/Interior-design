package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.ClientRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRecordRepository extends JpaRepository<ClientRecord, UUID> {
}
