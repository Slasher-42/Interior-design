package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.Role;
import com.ced.Auth.Security.Service.domain.SystemSettings;
import com.ced.Auth.Security.Service.domain.User;
import com.ced.Auth.Security.Service.dto.LoginRequest;
import com.ced.Auth.Security.Service.event.KafkaEventPublisher;
import com.ced.Auth.Security.Service.exception.AuthException;
import com.ced.Auth.Security.Service.repository.PasswordResetTokenRepository;
import com.ced.Auth.Security.Service.repository.UserRepository;
import com.ced.Auth.Security.Service.repository.VerificationTokenRepository;
import com.ced.Auth.Security.Service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceLockoutTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuditService auditService;
    @Mock
    private SystemSettingsService systemSettingsService;
    @Mock
    private MfaService mfaService;
    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private AuthService authService;

    private User user;
    private SystemSettings settings;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashed")
                .role(Role.CLIENT)
                .verified(true)
                .enabled(true)
                .failedLoginAttempts(0)
                .build();

        settings = SystemSettings.builder()
                .maxFailedLoginAttempts(3)
                .accountLockMinutes(15)
                .build();

        when(systemSettingsService.getOrCreate()).thenReturn(settings);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
    }

    @Test
    void locksAccountAfterReachingMaxFailedAttempts() {
        user.setFailedLoginAttempts(2);

        assertThrows(AuthException.class, () ->
                authService.login(new LoginRequest(user.getEmail(), "wrong-password"), "127.0.0.1"));

        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getAccountLockedUntil());
        verify(auditService).log(eq(user.getId()), eq(AuditAction.ACCOUNT_LOCKED), anyString(), eq("127.0.0.1"));
    }

    @Test
    void doesNotLockAccountBelowMaxFailedAttempts() {
        assertThrows(AuthException.class, () ->
                authService.login(new LoginRequest(user.getEmail(), "wrong-password"), "127.0.0.1"));

        assertEquals(1, user.getFailedLoginAttempts());
        assertNull(user.getAccountLockedUntil());
        verify(auditService, never()).log(eq(user.getId()), eq(AuditAction.ACCOUNT_LOCKED), anyString(), any());
        verify(auditService).log(eq(user.getId()), eq(AuditAction.LOGIN_FAILURE), anyString(), eq("127.0.0.1"));
    }
}
