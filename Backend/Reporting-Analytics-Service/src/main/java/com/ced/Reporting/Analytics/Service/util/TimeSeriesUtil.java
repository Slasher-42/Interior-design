package com.ced.Reporting.Analytics.Service.util;

import com.ced.Reporting.Analytics.Service.dto.TimeSeriesPoint;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TimeSeriesUtil {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);

    private TimeSeriesUtil() {
    }

    public static <T> List<TimeSeriesPoint> bucketCountByMonth(List<T> items, Function<T, Instant> timestampExtractor) {
        Map<String, Long> grouped = items.stream()
                .filter(item -> timestampExtractor.apply(item) != null)
                .collect(Collectors.groupingBy(item -> MONTH_FORMATTER.format(timestampExtractor.apply(item)),
                        TreeMap::new, Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> new TimeSeriesPoint(e.getKey(), BigDecimal.valueOf(e.getValue())))
                .toList();
    }

    public static <T> List<TimeSeriesPoint> bucketSumByMonth(List<T> items, Function<T, Instant> timestampExtractor,
                                                              Function<T, BigDecimal> valueExtractor) {
        Map<String, BigDecimal> grouped = items.stream()
                .filter(item -> timestampExtractor.apply(item) != null)
                .collect(Collectors.groupingBy(item -> MONTH_FORMATTER.format(timestampExtractor.apply(item)),
                        TreeMap::new, Collectors.reducing(BigDecimal.ZERO, valueExtractor, BigDecimal::add)));
        return grouped.entrySet().stream()
                .map(e -> new TimeSeriesPoint(e.getKey(), e.getValue()))
                .toList();
    }
}
