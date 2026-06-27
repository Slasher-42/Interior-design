package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.FeedbackRecord;
import com.ced.Reporting.Analytics.Service.repository.FeedbackRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackAnalyticsService {

    private final FeedbackRecordRepository feedbackRecordRepository;

    @Value("${app.feedback.low-rating-threshold:3}")
    private int lowRatingThreshold;

    @Transactional
    public void recordSubmitted(UUID feedbackId, UUID clientId, UUID projectId, Integer rating, Instant submittedAt) {
        feedbackRecordRepository.save(FeedbackRecord.builder()
                .id(feedbackId)
                .clientId(clientId)
                .projectId(projectId)
                .rating(rating)
                .lowRatingFlag(rating != null && rating < lowRatingThreshold)
                .submittedAt(submittedAt)
                .build());
    }

    public Double averageRating() {
        List<FeedbackRecord> all = feedbackRecordRepository.findAll();
        return all.isEmpty() ? null : all.stream().mapToInt(FeedbackRecord::getRating).average().orElse(0.0);
    }

    public long lowRatedCount() {
        return feedbackRecordRepository.countByLowRatingFlagTrue();
    }

    public List<FeedbackRecord> lowRatedFeedback() {
        return feedbackRecordRepository.findByLowRatingFlagTrue();
    }

    public List<FeedbackRecord> byClient(UUID clientId) {
        return feedbackRecordRepository.findByClientId(clientId);
    }
}
