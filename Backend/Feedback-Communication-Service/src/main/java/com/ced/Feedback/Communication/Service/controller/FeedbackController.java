package com.ced.Feedback.Communication.Service.controller;

import com.ced.Feedback.Communication.Service.domain.FeedbackStatus;
import com.ced.Feedback.Communication.Service.dto.FeedbackResponse;
import com.ced.Feedback.Communication.Service.dto.SubmitFeedbackRequest;
import com.ced.Feedback.Communication.Service.security.CurrentUser;
import com.ced.Feedback.Communication.Service.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> search(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) FeedbackStatus status,
            @RequestParam(required = false) Boolean hasComplaint,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FeedbackResponse> result = feedbackService
                .search(clientId, projectId, status, hasComplaint, maxRating, CurrentUser.id(), CurrentUser.role(), PageRequest.of(page, size))
                .map(FeedbackResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getById(@PathVariable UUID id) {
        var feedback = feedbackService.getByIdForCaller(id, CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(FeedbackResponse.from(feedback));
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<FeedbackResponse> submit(@PathVariable UUID id, @Valid @RequestBody SubmitFeedbackRequest request) {
        var feedback = feedbackService.submit(id, CurrentUser.id(), request);
        return ResponseEntity.ok(FeedbackResponse.from(feedback));
    }
}
