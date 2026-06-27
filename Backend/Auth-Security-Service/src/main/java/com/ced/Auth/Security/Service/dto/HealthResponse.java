package com.ced.Auth.Security.Service.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record HealthResponse(
        String status,
        long uptimeMs,
        MemoryInfo memory,
        String databaseStatus,
        Instant checkedAt
) {
    @Builder
    public record MemoryInfo(long usedBytes, long freeBytes, long totalBytes, long maxBytes) {
    }
}
