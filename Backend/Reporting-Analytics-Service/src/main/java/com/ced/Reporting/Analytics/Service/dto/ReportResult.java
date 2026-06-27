package com.ced.Reporting.Analytics.Service.dto;

import java.util.List;

public record ReportResult(List<String> columns, List<List<String>> rows, String summary) {
}
