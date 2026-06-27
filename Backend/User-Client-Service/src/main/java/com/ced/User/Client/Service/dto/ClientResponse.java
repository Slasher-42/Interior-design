package com.ced.User.Client.Service.dto;

import com.ced.User.Client.Service.domain.Client;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ClientResponse(
        UUID id,
        UUID userId,
        String fullName,
        String email,
        String phone,
        String organizationName,
        String industry,
        String country,
        String city,
        String website,
        Instant createdAt
) {
    public static ClientResponse from(Client c) {
        return ClientResponse.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .organizationName(c.getOrganizationName())
                .industry(c.getIndustry())
                .country(c.getCountry())
                .city(c.getCity())
                .website(c.getWebsite())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
