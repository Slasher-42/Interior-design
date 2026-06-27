package com.ced.Reporting.Analytics.Service.domain;

/**
 * Only the two states this service can actually observe (quotation.created, quotation.approved) -
 * rejection is not published, so a rejected quotation will still appear "pending" here.
 */
public enum QuotationRecordStatus {
    PENDING_APPROVAL,
    APPROVED
}
