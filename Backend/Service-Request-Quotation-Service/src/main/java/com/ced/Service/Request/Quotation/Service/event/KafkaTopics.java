package com.ced.Service.Request.Quotation.Service.event;

public final class KafkaTopics {

    public static final String SERVICE_REQUEST_CREATED = "service.request.created";
    public static final String SERVICE_REQUEST_ASSIGNED = "service.request.assigned";
    public static final String QUOTATION_CREATED = "quotation.created";
    public static final String QUOTATION_APPROVED = "quotation.approved";

    private KafkaTopics() {
    }
}
