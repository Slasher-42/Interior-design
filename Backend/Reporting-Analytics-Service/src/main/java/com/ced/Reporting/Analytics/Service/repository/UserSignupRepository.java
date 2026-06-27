package com.ced.Reporting.Analytics.Service.repository;

import com.ced.Reporting.Analytics.Service.domain.UserSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSignupRepository extends JpaRepository<UserSignup, UUID> {
}
