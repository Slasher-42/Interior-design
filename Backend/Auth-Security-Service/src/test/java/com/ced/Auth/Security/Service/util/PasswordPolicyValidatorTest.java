package com.ced.Auth.Security.Service.util;

import com.ced.Auth.Security.Service.domain.SystemSettings;
import com.ced.Auth.Security.Service.exception.AuthException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyValidatorTest {

    private final SystemSettings settings = SystemSettings.builder().build();

    @Test
    void acceptsPasswordMeetingAllRequirements() {
        assertDoesNotThrow(() -> PasswordPolicyValidator.validate("Str0ng!Pass", settings));
    }

    @Test
    void rejectsPasswordShorterThanMinLength() {
        assertThrows(AuthException.class, () -> PasswordPolicyValidator.validate("S1!a", settings));
    }

    @Test
    void rejectsPasswordMissingUppercase() {
        assertThrows(AuthException.class, () -> PasswordPolicyValidator.validate("str0ng!pass", settings));
    }

    @Test
    void rejectsPasswordMissingNumber() {
        assertThrows(AuthException.class, () -> PasswordPolicyValidator.validate("Strong!Pass", settings));
    }

    @Test
    void rejectsPasswordMissingSpecialChar() {
        assertThrows(AuthException.class, () -> PasswordPolicyValidator.validate("Str0ngPass", settings));
    }
}
