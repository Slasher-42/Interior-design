package com.ced.Reporting.Analytics.Service.dto;

/**
 * Local mirror of the response shape returned by User-Client-Service's
 * GET /clients/segments/summary, fetched live over HTTP rather than via Kafka.
 */
public record ClientSegmentSummaryResponse(
        String industry,
        String country,
        String city,
        long total
) {
}
