package com.ced.Project.Task.Service.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CompleteProjectRequest(
        @DecimalMin(value = "0", message = "finalCost cannot be negative") BigDecimal finalCost
) {
}
