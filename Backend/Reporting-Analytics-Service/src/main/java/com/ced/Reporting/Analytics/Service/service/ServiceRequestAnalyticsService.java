package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.Priority;
import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecord;
import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecordStatus;
import com.ced.Reporting.Analytics.Service.repository.ServiceRequestRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestAnalyticsService {

    private final ServiceRequestRecordRepository serviceRequestRecordRepository;

    @Transactional
    public void recordCreated(UUID requestId, UUID clientId, String category, Priority priority, Instant createdAt) {
        serviceRequestRecordRepository.save(ServiceRequestRecord.builder()
                .id(requestId)
                .clientId(clientId)
                .category(category)
                .priority(priority)
                .createdAt(createdAt)
                .build());
    }

    @Transactional
    public void recordAssigned(UUID requestId) {
        serviceRequestRecordRepository.findById(requestId).ifPresent(record -> {
            record.setStatus(ServiceRequestRecordStatus.ASSIGNED);
            serviceRequestRecordRepository.save(record);
        });
    }

    public long totalRequests() {
        return serviceRequestRecordRepository.count();
    }

    public Map<String, Long> byCategory() {
        return serviceRequestRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(ServiceRequestRecord::getCategory, Collectors.counting()));
    }

    public Map<String, Long> byPriority() {
        return serviceRequestRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getPriority().name(), Collectors.counting()));
    }

    public List<ServiceRequestRecord> pending() {
        return serviceRequestRecordRepository.findByStatus(ServiceRequestRecordStatus.PENDING);
    }
}
