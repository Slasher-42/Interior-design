package com.ced.Project.Task.Service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectSetupRequest(
        @NotNull UUID projectManagerId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
