package com.ced.Feedback.Communication.Service.service;

import com.ced.Feedback.Communication.Service.domain.Notification;
import com.ced.Feedback.Communication.Service.domain.Role;
import com.ced.Feedback.Communication.Service.exception.AppException;
import com.ced.Feedback.Communication.Service.repository.NotificationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification notifyUser(UUID recipientId, String title, String message, UUID referenceId) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification broadcastToRole(Role role, String title, String message, UUID referenceId) {
        Notification notification = Notification.builder()
                .recipientRole(role)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .build();
        return notificationRepository.save(notification);
    }

    public Page<Notification> listForCaller(UUID callerId, Role callerRole, Boolean unreadOnly, Pageable pageable) {
        return notificationRepository.findAll(buildMineSpec(callerId, callerRole, unreadOnly), pageable);
    }

    public long unreadCount(UUID callerId, Role callerRole) {
        return notificationRepository.count(buildMineSpec(callerId, callerRole, true));
    }

    @Transactional
    public void markRead(UUID id, UUID callerId, Role callerRole) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found", HttpStatus.NOT_FOUND));
        if (!isMine(notification, callerId, callerRole)) {
            throw new AppException("Notification not found", HttpStatus.NOT_FOUND);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllRead(UUID callerId, Role callerRole) {
        List<Notification> mine = notificationRepository.findAll(buildMineSpec(callerId, callerRole, true));
        mine.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(mine);
    }

    private Specification<Notification> buildMineSpec(UUID callerId, Role callerRole, Boolean unreadOnly) {
        return (root, cq, cb) -> {
            Predicate mine = cb.or(
                    cb.equal(root.get("recipientId"), callerId),
                    cb.equal(root.get("recipientRole"), callerRole)
            );
            if (Boolean.TRUE.equals(unreadOnly)) {
                return cb.and(mine, cb.isFalse(root.get("read")));
            }
            return mine;
        };
    }

    private boolean isMine(Notification notification, UUID callerId, Role callerRole) {
        return (notification.getRecipientId() != null && notification.getRecipientId().equals(callerId))
                || notification.getRecipientRole() == callerRole;
    }
}
