package com.ced.Auth.Security.Service.config;

import com.ced.Auth.Security.Service.domain.Role;
import com.ced.Auth.Security.Service.domain.User;
import com.ced.Auth.Security.Service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = User.builder()
                .fullName("Super Admin")
                .email(defaultAdminEmail)
                .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                .role(Role.ADMIN)
                .verified(true)
                .build();
        userRepository.save(admin);

        log.info("Seeded default admin account with email {}. Change the password after first login.", defaultAdminEmail);
    }
}
