package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.AuditAction;
import com.ced.Auth.Security.Service.domain.User;
import com.ced.Auth.Security.Service.dto.MfaSetupResponse;
import com.ced.Auth.Security.Service.exception.AuthException;
import com.ced.Auth.Security.Service.repository.UserRepository;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MfaService {

    private static final String ISSUER = "IDSMS";

    private final UserRepository userRepository;
    private final AuditService auditService;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    @Transactional
    public MfaSetupResponse setup(User user) {
        String secret = secretGenerator.generate();
        user.setMfaSecret(secret);
        userRepository.save(user);
        return MfaSetupResponse.builder()
                .secret(secret)
                .otpAuthUrl(buildOtpAuthUri(user.getEmail(), secret))
                .build();
    }

    @Transactional
    public void enable(User user, String code, String ipAddress) {
        requireSecret(user);
        if (!codeVerifier.isValidCode(user.getMfaSecret(), code)) {
            throw new AuthException("Invalid MFA code", HttpStatus.BAD_REQUEST);
        }
        user.setMfaEnabled(true);
        userRepository.save(user);
        auditService.log(user.getId(), AuditAction.MFA_ENABLED, "MFA enabled", ipAddress);
    }

    @Transactional
    public void disable(User user, String code, String ipAddress) {
        requireSecret(user);
        if (!codeVerifier.isValidCode(user.getMfaSecret(), code)) {
            throw new AuthException("Invalid MFA code", HttpStatus.BAD_REQUEST);
        }
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        auditService.log(user.getId(), AuditAction.MFA_DISABLED, "MFA disabled", ipAddress);
    }

    public boolean verifyCode(User user, String code) {
        return user.getMfaSecret() != null && codeVerifier.isValidCode(user.getMfaSecret(), code);
    }

    @Transactional
    public MfaSetupResponse setup(UUID userId) {
        return setup(loadUser(userId));
    }

    @Transactional
    public void enable(UUID userId, String code, String ipAddress) {
        enable(loadUser(userId), code, ipAddress);
    }

    @Transactional
    public void disable(UUID userId, String code, String ipAddress) {
        disable(loadUser(userId), code, ipAddress);
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));
    }

    private String buildOtpAuthUri(String email, String secret) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        return data.getUri();
    }

    private void requireSecret(User user) {
        if (user.getMfaSecret() == null) {
            throw new AuthException("MFA setup has not been initiated", HttpStatus.BAD_REQUEST);
        }
    }
}
