package com.ced.Reporting.Analytics.Service.client;

import com.ced.Reporting.Analytics.Service.dto.ClientSegmentSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Synchronous HTTP call to User-Client-Service - the one cross-service read this spec describes
 * as a live pull rather than an event ("pulls segmentation summaries from the User & Client
 * Service"), since segmentation is a complex derived query not worth replicating via Kafka.
 * The caller's own JWT is forwarded so User-Client-Service's existing role check is satisfied.
 */
@Slf4j
@Component
public class UserClientServiceClient {

    private final RestClient restClient;

    public UserClientServiceClient(@Value("${app.services.user-client-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<ClientSegmentSummaryResponse> fetchSegmentSummary(String authorizationHeader) {
        try {
            ClientSegmentSummaryResponse[] result = restClient.get()
                    .uri("/clients/segments/summary")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(ClientSegmentSummaryResponse[].class);
            return result != null ? List.of(result) : List.of();
        } catch (Exception e) {
            log.warn("Failed to fetch client segmentation summary from User-Client-Service: {}", e.getMessage());
            return List.of();
        }
    }
}
