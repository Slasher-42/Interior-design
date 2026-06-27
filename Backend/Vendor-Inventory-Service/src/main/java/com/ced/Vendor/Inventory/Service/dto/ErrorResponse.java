package com.ced.Vendor.Inventory.Service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
        String error,
        String message,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
}
