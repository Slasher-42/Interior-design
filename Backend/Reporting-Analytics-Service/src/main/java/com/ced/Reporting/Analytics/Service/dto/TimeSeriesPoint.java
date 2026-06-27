package com.ced.Reporting.Analytics.Service.dto;

import java.math.BigDecimal;

public record TimeSeriesPoint(String period, BigDecimal value) {
}
