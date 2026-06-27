package com.ced.User.Client.Service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String fullName,
        String email,
        String phone,
        String organizationName,
        String industry,
        String country,
        String city,
        String website
) {
}
