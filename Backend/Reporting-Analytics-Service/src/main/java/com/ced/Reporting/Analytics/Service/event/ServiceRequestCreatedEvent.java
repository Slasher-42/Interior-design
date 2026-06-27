package com.ced.Reporting.Analytics.Service.event;

import com.ced.Reporting.Analytics.Service.domain.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Service-Request-Quotation-Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestCreatedEvent {
    private UUID requestId;
    private UUID clientId;
    private String category;
    private Priority priority;
    private String description;
    private Instant createdAt;
}
