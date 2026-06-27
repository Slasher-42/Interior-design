package com.ced.Project.Task.Service.dto;

import com.ced.Project.Task.Service.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        @NotNull UUID assignedUserId,
        @NotNull Priority priority,
        LocalDate deadline
) {
}
