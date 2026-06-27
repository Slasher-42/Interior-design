package com.ced.Feedback.Communication.Service.event;

public final class KafkaTopics {

    // Consumed - this service is the single consumer responsible for every notification/message
    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_VERIFIED = "user.verified";
    public static final String CLIENT_CREATED = "client.created";
    public static final String SERVICE_REQUEST_CREATED = "service.request.created";
    public static final String SERVICE_REQUEST_ASSIGNED = "service.request.assigned";
    public static final String QUOTATION_CREATED = "quotation.created";
    public static final String QUOTATION_APPROVED = "quotation.approved";
    public static final String PROJECT_CREATED = "project.created";
    public static final String TASK_ASSIGNED = "task.assigned";
    public static final String TASK_COMPLETED = "task.completed";
    public static final String DOCUMENT_APPROVED = "document.approved";
    public static final String PURCHASE_ORDER_CREATED = "purchase.order.created";
    public static final String PROJECT_COMPLETED = "project.completed";

    // Published by this service
    public static final String FEEDBACK_SUBMITTED = "feedback.submitted";

    private KafkaTopics() {
    }
}
