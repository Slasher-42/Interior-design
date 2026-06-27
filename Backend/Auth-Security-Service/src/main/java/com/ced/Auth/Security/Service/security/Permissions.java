package com.ced.Auth.Security.Service.security;

import com.ced.Auth.Security.Service.domain.Role;

import java.util.List;
import java.util.Map;

public final class Permissions {

    private static final Map<Role, List<String>> ROLE_PERMISSIONS = Map.of(
            Role.ADMIN, List.of("USER_MANAGE", "SYSTEM_MANAGE", "AUDIT_VIEW"),
            Role.PROJECT_MANAGER, List.of("PROJECT_MANAGE", "TASK_MANAGE", "CLIENT_VIEW"),
            Role.DESIGNER, List.of("TASK_VIEW", "DOCUMENT_MANAGE"),
            Role.SALES_TEAM, List.of("CLIENT_MANAGE", "REQUEST_MANAGE", "QUOTATION_MANAGE"),
            Role.CLIENT, List.of("REQUEST_SUBMIT", "FEEDBACK_SUBMIT")
    );

    private Permissions() {
    }

    public static List<String> forRole(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, List.of());
    }
}
