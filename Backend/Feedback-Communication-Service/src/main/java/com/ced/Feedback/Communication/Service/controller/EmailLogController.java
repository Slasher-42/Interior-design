package com.ced.Feedback.Communication.Service.controller;

import com.ced.Feedback.Communication.Service.dto.EmailLogResponse;
import com.ced.Feedback.Communication.Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email-logs")
@RequiredArgsConstructor
public class EmailLogController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<Page<EmailLogResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EmailLogResponse> result = emailService
                .list(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt")))
                .map(EmailLogResponse::from);
        return ResponseEntity.ok(result);
    }
}
