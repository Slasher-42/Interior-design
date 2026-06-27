package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.client.UserClientServiceClient;
import com.ced.Reporting.Analytics.Service.domain.ClientRecord;
import com.ced.Reporting.Analytics.Service.dto.ClientSegmentSummaryResponse;
import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;
import com.ced.Reporting.Analytics.Service.repository.ClientRecordRepository;
import com.ced.Reporting.Analytics.Service.util.TimeSeriesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAnalyticsService {

    private final ClientRecordRepository clientRecordRepository;
    private final UserClientServiceClient userClientServiceClient;

    @Transactional
    public void recordClientCreated(UUID clientId, String name, Instant createdAt) {
        clientRecordRepository.save(ClientRecord.builder()
                .id(clientId)
                .name(name)
                .createdAt(createdAt)
                .build());
    }

    public long totalClients() {
        return clientRecordRepository.count();
    }

    public List<TimeSeriesPoint> growthTrend() {
        return TimeSeriesUtil.bucketCountByMonth(clientRecordRepository.findAll(), ClientRecord::getCreatedAt);
    }

    public List<ClientSegmentSummaryResponse> segmentation(String authorizationHeader) {
        return userClientServiceClient.fetchSegmentSummary(authorizationHeader);
    }
}
