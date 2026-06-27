package com.ced.Feedback.Communication.Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Either a specific recipient (a single user) or a role broadcast - never both. Role
     * broadcasts share one read flag across everyone with that role, rather than tracking
     * per-user read state, since this platform has no per-role membership roster to join against.
     */
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    private Role recipientRole;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    private UUID referenceId;

    @Builder.Default
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
