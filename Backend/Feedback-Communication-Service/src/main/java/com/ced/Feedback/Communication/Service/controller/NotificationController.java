package com.ced.Feedback.Communication.Service.controller;

import com.ced.Feedback.Communication.Service.dto.MessageResponse;
import com.ced.Feedback.Communication.Service.dto.NotificationResponse;
import com.ced.Feedback.Communication.Service.dto.UnreadCountResponse;
import com.ced.Feedback.Communication.Service.security.CurrentUser;
import com.ced.Feedback.Communication.Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<Page<NotificationResponse>> mine(
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NotificationResponse> result = notificationService
                .listForCaller(CurrentUser.id(), CurrentUser.role(), unreadOnly,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(NotificationResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<UnreadCountResponse> unreadCount() {
        long count = notificationService.unreadCount(CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markRead(@PathVariable UUID id) {
        notificationService.markRead(id, CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<MessageResponse> markAllRead() {
        notificationService.markAllRead(CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }
}
