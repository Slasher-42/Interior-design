package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final DataSource dataSource;

    public HealthResponse check() {
        String databaseStatus = checkDatabase();
        Runtime runtime = Runtime.getRuntime();

        return HealthResponse.builder()
                .status("UP".equals(databaseStatus) ? "UP" : "DEGRADED")
                .uptimeMs(ManagementFactory.getRuntimeMXBean().getUptime())
                .memory(HealthResponse.MemoryInfo.builder()
                        .usedBytes(runtime.totalMemory() - runtime.freeMemory())
                        .freeBytes(runtime.freeMemory())
                        .totalBytes(runtime.totalMemory())
                        .maxBytes(runtime.maxMemory())
                        .build())
                .databaseStatus(databaseStatus)
                .checkedAt(Instant.now())
                .build();
    }

    private String checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}
