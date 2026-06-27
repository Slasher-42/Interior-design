package com.ced.Auth.Security.Service.controller;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.dto.*;
import com.ced.Auth.Security.Service.security.CurrentUser;
import com.ced.Auth.Security.Service.service.AuditService;
import com.ced.Auth.Security.Service.service.AuthService;
import com.ced.Auth.Security.Service.service.HealthService;
import com.ced.Auth.Security.Service.service.SystemSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final SystemSettingsService systemSettingsService;
    private final AuditService auditService;
    private final HealthService healthService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.createUserByAdmin(request, CurrentUser.id(), httpRequest.getRemoteAddr()));
    }

    @GetMapping("/settings")
    public ResponseEntity<SystemSettingsResponse> getSettings() {
        return ResponseEntity.ok(SystemSettingsResponse.from(systemSettingsService.getOrCreate()));
    }

    @PutMapping("/settings")
    public ResponseEntity<SystemSettingsResponse> updateSettings(@Valid @RequestBody UpdateSystemSettingsRequest request,
                                                                   HttpServletRequest httpRequest) {
        return ResponseEntity.ok(SystemSettingsResponse.from(
                systemSettingsService.update(request, CurrentUser.id(), httpRequest.getRemoteAddr())));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) java.util.UUID userId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponse> result = auditService.search(userId, action, PageRequest.of(page, size))
                .map(AuditLogResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(healthService.check());
    }
}
