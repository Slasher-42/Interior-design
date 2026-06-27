package com.ced.Project.Task.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCompletedEvent {
    private UUID projectId;
    private UUID clientId;
    private Instant completedAt;
    private BigDecimal finalCost;
}
