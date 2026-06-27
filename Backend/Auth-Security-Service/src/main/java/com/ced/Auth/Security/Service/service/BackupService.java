package com.ced.Auth.Security.Service.service;

import com.ced.Auth.Security.Service.domain.AuditAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final AuditService auditService;

    @Value("${app.backup.dir:./backups}")
    private String backupDir;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Scheduled(cron = "${app.backup.cron:0 0 2 * * *}")
    public void runScheduledBackup() {
        auditService.log(null, AuditAction.BACKUP_TRIGGERED, "Scheduled database backup triggered", "system");
        try {
            triggerBackup();
            auditService.log(null, AuditAction.BACKUP_SUCCEEDED, "Scheduled database backup completed", "system");
        } catch (Exception e) {
            log.error("Database backup failed", e);
            auditService.log(null, AuditAction.BACKUP_FAILED, "Database backup failed: " + e.getMessage(), "system");
        }
    }

    private void triggerBackup() throws Exception {
        File dir = new File(backupDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Unable to create backup directory: " + backupDir);
        }

        String dbName = extractDatabaseName(datasourceUrl);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File outputFile = new File(dir, dbName + "_" + timestamp + ".sql");

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "--no-password",
                "--username=" + datasourceUsername,
                "--file=" + outputFile.getAbsolutePath(),
                dbName
        );
        pb.environment().put("PGPASSWORD", datasourcePassword);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        boolean finished = process.waitFor(5, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("pg_dump timed out");
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException("pg_dump exited with code " + process.exitValue());
        }
    }

    private String extractDatabaseName(String jdbcUrl) {
        int lastSlash = jdbcUrl.lastIndexOf('/');
        String tail = jdbcUrl.substring(lastSlash + 1);
        int queryIndex = tail.indexOf('?');
        return queryIndex >= 0 ? tail.substring(0, queryIndex) : tail;
    }
}
