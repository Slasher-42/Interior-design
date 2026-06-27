package com.ced.Reporting.Analytics.Service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Read-model fact row populated from user.registered. The ID is the source userId (not
 * auto-generated), so re-delivery of the same event safely upserts rather than double-counting.
 */
@Entity
@Table(name = "user_signups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignup {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Instant registeredAt;
}
