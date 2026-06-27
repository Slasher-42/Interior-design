package com.ced.Reporting.Analytics.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of the event published by Service-Request-Quotation-Service. Consumed here even
 * though it isn't in the spec's explicit "consumes X" list for this service, because without it
 * "pending service requests" on the sales dashboard would be unobservable - this is an
 * already-published event, so consuming it adds no upstream changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestAssignedEvent {
    private UUID requestId;
    private UUID clientId;
    private UUID assignedDesignerId;
    private Instant assignedAt;
}
