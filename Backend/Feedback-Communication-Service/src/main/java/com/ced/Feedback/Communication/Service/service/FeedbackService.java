package com.ced.Feedback.Communication.Service.service;

import com.ced.Feedback.Communication.Service.domain.Feedback;
import com.ced.Feedback.Communication.Service.domain.FeedbackStatus;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.dto.SubmitFeedbackRequest;
import com.ced.Feedback.Communication.Service.event.FeedbackSubmittedEvent;
import com.ced.Feedback.Communication.Service.event.KafkaEventPublisher;
import com.ced.Feedback.Communication.Service.exception.AppException;
import com.ced.Feedback.Communication.Service.repository.FeedbackRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final NotificationService notificationService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Value("${app.feedback.low-rating-threshold:3}")
    private int lowRatingThreshold;

    @Transactional
    public Feedback openFeedbackRequest(UUID projectId, UUID clientId) {
        Feedback feedback = Feedback.builder()
                .projectId(projectId)
                .clientId(clientId)
                .build();
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback submit(UUID id, UUID clientId, SubmitFeedbackRequest request) {
        Feedback feedback = getById(id);
        if (!feedback.getClientId().equals(clientId)) {
            throw new AppException("Feedback not found", HttpStatus.NOT_FOUND);
        }
        if (feedback.getStatus() != FeedbackStatus.OPEN) {
            throw new AppException("This feedback has already been submitted", HttpStatus.CONFLICT);
        }

        feedback.setOverallRating(request.overallRating());
        feedback.setDesignQualityRating(request.designQualityRating());
        feedback.setResponsivenessRating(request.responsivenessRating());
        feedback.setTimelineAdherenceRating(request.timelineAdherenceRating());
        feedback.setComments(request.comments());
        feedback.setComplaint(request.complaint());
        feedback.setHasComplaint(StringUtils.hasText(request.complaint()));
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        feedback.setSubmittedAt(Instant.now());
        feedback = feedbackRepository.save(feedback);

        kafkaEventPublisher.publishFeedbackSubmitted(FeedbackSubmittedEvent.builder()
                .feedbackId(feedback.getId())
                .clientId(feedback.getClientId())
                .projectId(feedback.getProjectId())
                .rating(feedback.getOverallRating())
                .submittedAt(feedback.getSubmittedAt())
                .build());

        if (feedback.getOverallRating() < lowRatingThreshold) {
            String alert = "A client rated their project " + feedback.getOverallRating()
                    + "/5 (below threshold). Feedback: " + feedback.getId();
            notificationService.broadcastToRole(Role.ADMIN, "Low satisfaction alert", alert, feedback.getProjectId());
            notificationService.broadcastToRole(Role.PROJECT_MANAGER, "Low satisfaction alert", alert, feedback.getProjectId());
        }

        return feedback;
    }

    public Feedback getById(UUID id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new AppException("Feedback not found", HttpStatus.NOT_FOUND));
    }

    public Feedback getByIdForCaller(UUID id, UUID callerId, Role callerRole) {
        Feedback feedback = getById(id);
        if (callerRole == Role.CLIENT && !feedback.getClientId().equals(callerId)) {
            throw new AppException("Feedback not found", HttpStatus.NOT_FOUND);
        }
        return feedback;
    }

    public Page<Feedback> search(UUID clientId, UUID projectId, FeedbackStatus status, Boolean hasComplaint,
                                  Integer maxRating, UUID callerId, Role callerRole, Pageable pageable) {
        UUID effectiveClientId = callerRole == Role.CLIENT ? callerId : clientId;

        Specification<Feedback> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (effectiveClientId != null) {
                predicates.add(cb.equal(root.get("clientId"), effectiveClientId));
            }
            if (projectId != null) {
                predicates.add(cb.equal(root.get("projectId"), projectId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (hasComplaint != null) {
                predicates.add(cb.equal(root.get("hasComplaint"), hasComplaint));
            }
            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("overallRating"), maxRating));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return feedbackRepository.findAll(spec, pageable);
    }
}
