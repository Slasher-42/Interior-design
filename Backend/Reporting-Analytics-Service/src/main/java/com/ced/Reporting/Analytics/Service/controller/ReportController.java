package com.ced.Reporting.Analytics.Service.controller;

import com.ced.Reporting.Analytics.Service.domain.ReportDimension;
import com.ced.Reporting.Analytics.Service.dto.ReportResult;
import com.ced.Reporting.Analytics.Service.service.ReportExcelExporter;
import com.ced.Reporting.Analytics.Service.service.ReportPdfExporter;
import com.ced.Reporting.Analytics.Service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportPdfExporter reportPdfExporter;
    private final ReportExcelExporter reportExcelExporter;

    @GetMapping("/custom")
    public ResponseEntity<Object> custom(
            @RequestParam ReportDimension dimension,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "json") String format) {
        ReportResult result = reportService.generate(dimension, from, to);
        String title = dimension.name().replace('_', ' ') + " Report";
        String fileBaseName = dimension.name().toLowerCase().replace('_', '-') + "-report";

        return switch (format.toLowerCase()) {
            case "pdf" -> ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileBaseName + ".pdf\"")
                    .body(reportPdfExporter.export(title, result));
            case "excel", "xlsx" -> ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileBaseName + ".xlsx\"")
                    .body(reportExcelExporter.export(title, result));
            default -> ResponseEntity.ok(result);
        };
    }
}
