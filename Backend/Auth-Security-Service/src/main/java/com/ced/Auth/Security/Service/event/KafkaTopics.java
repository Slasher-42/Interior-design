package com.ced.Auth.Security.Service.event;

public final class KafkaTopics {

    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_VERIFIED = "user.verified";
    public static final String PASSWORD_RESET_REQUESTED = "password.reset.requested";
    public static final String AUTH_AUDIT_LOGGED = "auth.audit.logged";

    private KafkaTopics() {
    }
}
