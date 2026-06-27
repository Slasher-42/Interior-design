package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.*;
import com.ced.Auth.Security.Service.dto.*;
import com.ced.Auth.Security.Service.event.KafkaEventPublisher;
import com.ced.Auth.Security.Service.event.PasswordResetRequestedEvent;
import com.ced.Auth.Security.Service.event.UserRegisteredEvent;
import com.ced.Auth.Security.Service.event.UserVerifiedEvent;
import com.ced.Auth.Security.Service.exception.AuthException;
import com.ced.Auth.Security.Service.repository.PasswordResetTokenRepository;
import com.ced.Auth.Security.Service.repository.UserRepository;
import com.ced.Auth.Security.Service.repository.VerificationTokenRepository;
import com.ced.Auth.Security.Service.security.JwtUtil;
import com.ced.Auth.Security.Service.util.PasswordPolicyValidator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long VERIFICATION_TOKEN_TTL_MS = 24L * 60 * 60 * 1000;
    private static final long PASSWORD_RESET_TOKEN_TTL_MS = 60L * 60 * 1000;

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;
    private final SystemSettingsService systemSettingsService;
    private final MfaService mfaService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public UserResponse register(RegisterRequest request, String ipAddress) {
        if (request.role() == Role.ADMIN) {
            throw new AuthException("Administrator accounts cannot be self-registered", HttpStatus.FORBIDDEN);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        PasswordPolicyValidator.validate(request.password(), systemSettingsService.getOrCreate());

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        user = userRepository.save(user);

        String token = issueVerificationToken(user.getId());

        kafkaEventPublisher.publishUserRegistered(UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .verificationToken(token)
                .createdAt(user.getCreatedAt())
                .build());

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse createUserByAdmin(CreateUserRequest request, UUID adminId, String ipAddress) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        PasswordPolicyValidator.validate(request.password(), systemSettingsService.getOrCreate());

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .verified(true)
                .build();
        user = userRepository.save(user);

        kafkaEventPublisher.publishUserRegistered(UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .verificationToken(null)
                .createdAt(user.getCreatedAt())
                .build());

        return UserResponse.from(user);
    }

    @Transactional
    public MessageResponse verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Invalid verification token", HttpStatus.BAD_REQUEST));

        if (verificationToken.isUsed() || verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Verification token has expired or already been used", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        user.setVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        kafkaEventPublisher.publishUserVerified(UserVerifiedEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .verifiedAt(Instant.now())
                .build());

        return new MessageResponse("Email verified successfully");
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        SystemSettings settings = systemSettingsService.getOrCreate();

        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null) {
            auditService.log(null, AuditAction.LOGIN_FAILURE, "Login attempt for unknown email: " + request.email(), ipAddress);
            throw new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        if (settings.isMaintenanceMode() && user.getRole() != Role.ADMIN) {
            throw new AuthException("The system is currently under maintenance. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (isAccountLocked(user)) {
            auditService.log(user.getId(), AuditAction.LOGIN_FAILURE, "Login attempt on locked account", ipAddress);
            throw new AuthException("Account is locked due to too many failed attempts. Try again later.", HttpStatus.LOCKED);
        }

        if (!user.isEnabled()) {
            throw new AuthException("This account has been deactivated", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            handleFailedLogin(user, settings, ipAddress);
            throw new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isVerified()) {
            throw new AuthException("Please verify your email before logging in", HttpStatus.FORBIDDEN);
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        if (settings.isMfaEnforced() && !user.isMfaEnabled()) {
            return AuthResponse.builder()
                    .mfaSetupRequired(true)
                    .userId(user.getId().toString())
                    .role(user.getRole().name())
                    .build();
        }

        if (user.isMfaEnabled()) {
            String challengeToken = jwtUtil.generateMfaChallengeToken(user.getId());
            return AuthResponse.builder()
                    .mfaRequired(true)
                    .challengeToken(challengeToken)
                    .userId(user.getId().toString())
                    .role(user.getRole().name())
                    .build();
        }

        return issueAccessToken(user, settings, ipAddress, true);
    }

    @Transactional
    public AuthResponse verifyMfaAndIssueToken(MfaVerifyRequest request, String ipAddress) {
        Claims claims;
        try {
            claims = jwtUtil.parseClaims(request.challengeToken());
        } catch (Exception e) {
            throw new AuthException("Invalid or expired MFA challenge token", HttpStatus.BAD_REQUEST);
        }
        if (!jwtUtil.isType(claims, JwtUtil.TYPE_MFA_CHALLENGE)) {
            throw new AuthException("Invalid MFA challenge token", HttpStatus.BAD_REQUEST);
        }

        UUID userId = jwtUtil.extractUserId(claims);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        if (!mfaService.verifyCode(user, request.code())) {
            auditService.log(user.getId(), AuditAction.LOGIN_FAILURE, "Invalid MFA code", ipAddress);
            throw new AuthException("Invalid MFA code", HttpStatus.UNAUTHORIZED);
        }

        auditService.log(user.getId(), AuditAction.MFA_VERIFIED, "MFA code verified during login", ipAddress);

        SystemSettings settings = systemSettingsService.getOrCreate();
        return issueAccessToken(user, settings, ipAddress, false);
    }

    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            List<PasswordResetToken> active = passwordResetTokenRepository.findByUserIdAndUsedFalse(user.getId());
            active.forEach(t -> t.setUsed(true));
            passwordResetTokenRepository.saveAll(active);

            Instant expiresAt = Instant.now().plusMillis(PASSWORD_RESET_TOKEN_TTL_MS);
            PasswordResetToken resetToken = passwordResetTokenRepository.save(PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .expiresAt(expiresAt)
                    .build());

            kafkaEventPublisher.publishPasswordResetRequested(PasswordResetRequestedEvent.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .resetToken(resetToken.getToken())
                    .expiresAt(expiresAt)
                    .build());

            auditService.log(user.getId(), AuditAction.PASSWORD_RESET_REQUESTED, "Password reset requested", null);
        });

        return new MessageResponse("If an account with that email exists, a password reset link has been sent");
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request, String ipAddress) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new AuthException("Invalid or expired password reset token", HttpStatus.BAD_REQUEST));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Invalid or expired password reset token", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        PasswordPolicyValidator.validate(request.newPassword(), systemSettingsService.getOrCreate());

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        auditService.log(user.getId(), AuditAction.PASSWORD_RESET_COMPLETED, "Password reset completed", ipAddress);

        return new MessageResponse("Password has been reset successfully");
    }

    private String issueVerificationToken(UUID userId) {
        VerificationToken token = verificationTokenRepository.save(VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiresAt(Instant.now().plusMillis(VERIFICATION_TOKEN_TTL_MS))
                .build());
        return token.getToken();
    }

    private boolean isAccountLocked(User user) {
        return user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(Instant.now());
    }

    private void handleFailedLogin(User user, SystemSettings settings, String ipAddress) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= settings.getMaxFailedLoginAttempts()) {
            user.setAccountLockedUntil(Instant.now().plusSeconds(settings.getAccountLockMinutes() * 60L));
            userRepository.save(user);
            auditService.log(user.getId(), AuditAction.ACCOUNT_LOCKED,
                    "Account locked after " + user.getFailedLoginAttempts() + " failed login attempts", ipAddress);
        } else {
            userRepository.save(user);
        }
        auditService.log(user.getId(), AuditAction.LOGIN_FAILURE, "Invalid password", ipAddress);
    }

    private AuthResponse issueAccessToken(User user, SystemSettings settings, String ipAddress, boolean logSuccess) {
        long expirationMs = settings.getSessionTimeoutMinutes() * 60_000L;
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole(), expirationMs);

        if (logSuccess) {
            auditService.log(user.getId(), AuditAction.LOGIN_SUCCESS, "Login successful", ipAddress);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresInMs(expirationMs)
                .userId(user.getId().toString())
                .role(user.getRole().name())
                .build();
    }
}
