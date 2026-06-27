package com.ced.Auth.Security.Service.controller;

import com.ced.Auth.Security.Service.dto.*;
import com.ced.Auth.Security.Service.security.CurrentUser;
import com.ced.Auth.Security.Service.service.AuthService;
import com.ced.Auth.Security.Service.service.MfaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MfaService mfaService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, httpRequest.getRemoteAddr()));
    }

    @GetMapping("/verify")
    public ResponseEntity<MessageResponse> verify(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.verifyMfaAndIssueToken(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa() {
        return ResponseEntity.ok(mfaService.setup(CurrentUser.id()));
    }

    @PostMapping("/mfa/enable")
    public ResponseEntity<MessageResponse> enableMfa(@Valid @RequestBody MfaCodeRequest request, HttpServletRequest httpRequest) {
        mfaService.enable(CurrentUser.id(), request.code(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(new MessageResponse("MFA has been enabled"));
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<MessageResponse> disableMfa(@Valid @RequestBody MfaCodeRequest request, HttpServletRequest httpRequest) {
        mfaService.disable(CurrentUser.id(), request.code(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(new MessageResponse("MFA has been disabled"));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.resetPassword(request, httpRequest.getRemoteAddr()));
    }
}
