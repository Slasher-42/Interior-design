package com.ced.Project.Task.Service.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateMilestoneRequest(
        @NotBlank String title,
        String description,
        LocalDate dueDate
) {
}
