package com.ced.User.Client.Service.dto;

import com.ced.User.Client.Service.domain.Role;
import com.ced.User.Client.Service.domain.UserProfile;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        Role role,
        String profileImageUrl,
        boolean verified,
        boolean active,
        String professionalTitle,
        String specialization,
        String department,
        String organizationName,
        String industry,
        String country,
        String city,
        String website,
        Instant createdAt
) {
    public static UserProfileResponse from(UserProfile p) {
        return UserProfileResponse.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .role(p.getRole())
                .profileImageUrl(p.getProfileImageUrl())
                .verified(p.isVerified())
                .active(p.isActive())
                .professionalTitle(p.getProfessionalTitle())
                .specialization(p.getSpecialization())
                .department(p.getDepartment())
                .organizationName(p.getOrganizationName())
                .industry(p.getIndustry())
                .country(p.getCountry())
                .city(p.getCity())
                .website(p.getWebsite())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
