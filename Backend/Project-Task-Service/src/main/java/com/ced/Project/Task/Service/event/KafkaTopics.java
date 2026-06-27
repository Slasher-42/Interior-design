package com.ced.Project.Task.Service.event;

public final class KafkaTopics {

    // Consumed (published by Service-Request-Quotation-Service / Vendor-Inventory-Service)
    public static final String QUOTATION_APPROVED = "quotation.approved";
    public static final String PURCHASE_ORDER_CREATED = "purchase.order.created";

    // Published by this service
    public static final String PROJECT_CREATED = "project.created";
    public static final String TASK_ASSIGNED = "task.assigned";
    public static final String TASK_COMPLETED = "task.completed";
    public static final String PROJECT_COMPLETED = "project.completed";

    private KafkaTopics() {
    }
}
