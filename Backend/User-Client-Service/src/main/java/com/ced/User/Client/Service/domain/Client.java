package com.ced.User.Client.Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Set when this client also has a login account (a self-registered CLIENT-role user).
     * Null when the client was added directly by Admin/Sales with no portal access.
     */
    private UUID userId;

    @Column(nullable = false)
    private String fullName;

    private String email;

    private String phone;

    private String organizationName;

    private String industry;

    private String country;

    private String city;

    private String website;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
