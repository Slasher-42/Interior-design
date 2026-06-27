package com.ced.User.Client.Service.service;

import com.ced.User.Client.Service.domain.Role;
import com.ced.User.Client.Service.domain.UserProfile;
import com.ced.User.Client.Service.dto.UpdateProfileDetailsRequest;
import com.ced.User.Client.Service.dto.UpdateProfileRequest;
import com.ced.User.Client.Service.dto.UserProfileResponse;
import com.ced.User.Client.Service.event.UserRegisteredEvent;
import com.ced.User.Client.Service.event.UserVerifiedEvent;
import com.ced.User.Client.Service.exception.AppException;
import com.ced.User.Client.Service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ClientService clientService;

    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        if (userProfileRepository.existsById(event.getUserId())) {
            return;
        }

        UserProfile profile = UserProfile.builder()
                .id(event.getUserId())
                .fullName(event.getFullName())
                .email(event.getEmail())
                .role(event.getRole())
                .build();
        profile = userProfileRepository.save(profile);

        if (profile.getRole() == Role.CLIENT) {
            clientService.createLinkedForUser(profile.getId(), profile.getFullName(), profile.getEmail());
        }
    }

    @Transactional
    public void handleUserVerified(UserVerifiedEvent event) {
        userProfileRepository.findById(event.getUserId()).ifPresent(profile -> {
            profile.setVerified(true);
            userProfileRepository.save(profile);
        });
    }

    public UserProfileResponse getOwnProfile(UUID userId) {
        return UserProfileResponse.from(findOrThrow(userId));
    }

    @Transactional
    public UserProfileResponse updateOwnProfile(UUID userId, UpdateProfileRequest request) {
        UserProfile profile = findOrThrow(userId);
        if (request.fullName() != null) {
            profile.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }
        if (request.profileImageUrl() != null) {
            profile.setProfileImageUrl(request.profileImageUrl());
        }
        return UserProfileResponse.from(userProfileRepository.save(profile));
    }

    @Transactional
    public UserProfileResponse updateOwnProfileDetails(UUID userId, UpdateProfileDetailsRequest request) {
        UserProfile profile = findOrThrow(userId);

        switch (profile.getRole()) {
            case DESIGNER -> {
                if (request.professionalTitle() != null) profile.setProfessionalTitle(request.professionalTitle());
                if (request.specialization() != null) profile.setSpecialization(request.specialization());
                if (request.country() != null) profile.setCountry(request.country());
                if (request.city() != null) profile.setCity(request.city());
            }
            case SALES_TEAM, PROJECT_MANAGER -> {
                if (request.department() != null) profile.setDepartment(request.department());
            }
            case CLIENT -> {
                if (request.organizationName() != null) profile.setOrganizationName(request.organizationName());
                if (request.industry() != null) profile.setIndustry(request.industry());
                if (request.country() != null) profile.setCountry(request.country());
                if (request.city() != null) profile.setCity(request.city());
                if (request.website() != null) profile.setWebsite(request.website());
                clientService.syncLinkedClientDetails(userId, request.organizationName(), request.industry(),
                        request.country(), request.city(), request.website());
            }
            case ADMIN -> {
                // no role-specific fields for administrators
            }
        }

        return UserProfileResponse.from(userProfileRepository.save(profile));
    }

    private UserProfile findOrThrow(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException("Profile not found", HttpStatus.NOT_FOUND));
    }
}
