package com.ced.Reporting.Analytics.Service.domain;

/**
 * Only the two states this service can actually observe from the events it consumes
 * (service.request.created and service.request.assigned) - rejection/closure are not published.
 */
public enum ServiceRequestRecordStatus {
    PENDING,
    ASSIGNED
}
