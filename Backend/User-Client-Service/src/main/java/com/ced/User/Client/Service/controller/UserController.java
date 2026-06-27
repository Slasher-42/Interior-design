package com.ced.User.Client.Service.controller;

import com.ced.User.Client.Service.domain.Role;
import com.ced.User.Client.Service.domain.UserProfile;
import com.ced.User.Client.Service.dto.*;
import com.ced.User.Client.Service.security.CurrentUser;
import com.ced.User.Client.Service.service.ProfileService;
import com.ced.User.Client.Service.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;
    private final UserAdminService userAdminService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getOwnProfile() {
        return ResponseEntity.ok(profileService.getOwnProfile(CurrentUser.id()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateOwnProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateOwnProfile(CurrentUser.id(), request));
    }

    @PutMapping("/me/details")
    public ResponseEntity<UserProfileResponse> updateOwnProfileDetails(@RequestBody UpdateProfileDetailsRequest request) {
        return ResponseEntity.ok(profileService.updateOwnProfileDetails(CurrentUser.id(), request));
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> search(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserProfileResponse> result = userAdminService.search(role, active, query, PageRequest.of(page, size))
                .map(UserProfileResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(UserProfileResponse.from(userAdminService.getById(id)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserProfileResponse> activate(@PathVariable UUID id) {
        UserProfile profile = userAdminService.setActive(id, true);
        return ResponseEntity.ok(UserProfileResponse.from(profile));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserProfileResponse> deactivate(@PathVariable UUID id) {
        UserProfile profile = userAdminService.setActive(id, false);
        return ResponseEntity.ok(UserProfileResponse.from(profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable UUID id) {
        userAdminService.delete(id, CurrentUser.id());
        return ResponseEntity.ok(new MessageResponse("User has been permanently deleted"));
    }
}
