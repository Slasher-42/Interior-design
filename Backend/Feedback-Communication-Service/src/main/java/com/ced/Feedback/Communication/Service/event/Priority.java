package com.ced.Feedback.Communication.Service.event;

/**
 * Local mirror of the Priority enum used inside consumed event payloads
 * (service.request.created, task.assigned). Not a domain concept of this service itself.
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}
