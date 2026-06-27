package com.ced.Service.Request.Quotation.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

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
