package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.FeedbackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRecordRepository extends JpaRepository<FeedbackRecord, UUID> {

    long countByLowRatingFlagTrue();

    List<FeedbackRecord> findByLowRatingFlagTrue();

    List<FeedbackRecord> findByClientId(UUID clientId);
}
