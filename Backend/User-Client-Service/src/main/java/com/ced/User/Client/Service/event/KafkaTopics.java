package com.ced.User.Client.Service.event;

public final class KafkaTopics {

    // Consumed (published by Auth-Security-Service)
    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_VERIFIED = "user.verified";

    // Published by this service
    public static final String CLIENT_CREATED = "client.created";
    public static final String USER_DELETED = "user.deleted";

    private KafkaTopics() {
    }
}
