package com.ced.Feedback.Communication.Service.controller;

import com.ced.Feedback.Communication.Service.dto.CommunicationLogResponse;
import com.ced.Feedback.Communication.Service.security.CurrentUser;
import com.ced.Feedback.Communication.Service.service.CommunicationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommunicationLogController {

    private final CommunicationLogService communicationLogService;

    @GetMapping("/clients/{clientId}/communications")
    public ResponseEntity<Page<CommunicationLogResponse>> listByClient(
            @PathVariable UUID clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunicationLogResponse> result = communicationLogService
                .listByClient(clientId, CurrentUser.id(), CurrentUser.role(),
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(CommunicationLogResponse::from);
        return ResponseEntity.ok(result);
    }
}
