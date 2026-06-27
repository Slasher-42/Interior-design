package com.ced.Auth.Security.Service.dto;

import com.ced.Auth.Security.Service.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String password,
        @NotNull Role role
) {
}
