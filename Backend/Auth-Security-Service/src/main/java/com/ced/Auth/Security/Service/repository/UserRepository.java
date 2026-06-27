package com.ced.Auth.Security.Service.repository;

import com.ced.Auth.Security.Service.domain.Role;
import com.ced.Auth.Security.Service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}
