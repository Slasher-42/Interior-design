package com.ced.User.Client.Service.dto;

import lombok.Builder;

@Builder
public record ClientSegmentSummaryResponse(
        String industry,
        String country,
        String city,
        long total
) {
    public static ClientSegmentSummaryResponse from(ClientSegmentSummary s) {
        return ClientSegmentSummaryResponse.builder()
                .industry(s.getIndustry())
                .country(s.getCountry())
                .city(s.getCity())
                .total(s.getTotal())
                .build();
    }
}
