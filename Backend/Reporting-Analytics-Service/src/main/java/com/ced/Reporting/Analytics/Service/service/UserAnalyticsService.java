package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.Role;
import com.ced.Reporting.Analytics.Service.domain.UserSignup;
import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;
import com.ced.Reporting.Analytics.Service.repository.UserSignupRepository;
import com.ced.Reporting.Analytics.Service.util.TimeSeriesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAnalyticsService {

    private final UserSignupRepository userSignupRepository;

    @Transactional
    public void recordSignup(UUID userId, Role role, Instant registeredAt) {
        userSignupRepository.save(UserSignup.builder()
                .id(userId)
                .role(role)
                .registeredAt(registeredAt)
                .build());
    }

    public long totalUsers() {
        return userSignupRepository.count();
    }

    public List<TimeSeriesPoint> signupTrend() {
        return TimeSeriesUtil.bucketCountByMonth(userSignupRepository.findAll(), UserSignup::getRegisteredAt);
    }
}
