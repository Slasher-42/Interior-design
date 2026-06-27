package com.ced.User.Client.Service.dto;

/**
 * Role-specific identity profile fields. Which fields apply depends on the caller's own role
 * (validated in ProfileService) - irrelevant fields for a given role are simply ignored.
 */
public record UpdateProfileDetailsRequest(
        String professionalTitle,
        String specialization,
        String department,
        String organizationName,
        String industry,
        String country,
        String city,
        String website
) {
}
