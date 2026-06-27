package com.ced.Feedback.Communication.Service.event;

/**
 * Local mirror of the QuotationStatus enum used inside the consumed quotation.created payload.
 */
public enum QuotationStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED
}
