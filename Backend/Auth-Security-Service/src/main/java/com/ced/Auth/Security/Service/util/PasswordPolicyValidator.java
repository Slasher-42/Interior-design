package com.ced.Auth.Security.Service.util;

import com.ced.Auth.Security.Service.domain.SystemSettings;
import com.ced.Auth.Security.Service.exception.AuthException;
import org.springframework.http.HttpStatus;

public final class PasswordPolicyValidator {

    private PasswordPolicyValidator() {
    }

    public static void validate(String password, SystemSettings settings) {
        StringBuilder violations = new StringBuilder();

        if (password.length() < settings.getPasswordMinLength()) {
            violations.append("Password must be at least ")
                    .append(settings.getPasswordMinLength())
                    .append(" characters long. ");
        }
        if (settings.isPasswordRequireUppercase() && password.chars().noneMatch(Character::isUpperCase)) {
            violations.append("Password must contain an uppercase letter. ");
        }
        if (settings.isPasswordRequireNumber() && password.chars().noneMatch(Character::isDigit)) {
            violations.append("Password must contain a number. ");
        }
        if (settings.isPasswordRequireSpecialChar() && password.chars().allMatch(Character::isLetterOrDigit)) {
            violations.append("Password must contain a special character. ");
        }

        if (!violations.isEmpty()) {
            throw new AuthException(violations.toString().trim(), HttpStatus.BAD_REQUEST);
        }
    }
}
