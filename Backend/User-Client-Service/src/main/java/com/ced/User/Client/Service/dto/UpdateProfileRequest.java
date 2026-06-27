package com.ced.User.Client.Service.dto;

public record UpdateProfileRequest(
        String fullName,
        String phone,
        String profileImageUrl
) {
}
