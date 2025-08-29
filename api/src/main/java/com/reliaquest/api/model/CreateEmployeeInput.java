package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateEmployeeInput(
        @NotBlank String name,
        @NotNull @Min(1) Integer salary,
        @NotNull @Min(16) @Max(75) Integer age,
        @NotBlank String title
) {
    public CreateEmployeeInput {
        name = name == null ? null : name.trim();
        title = title == null ? null : title.trim();
    }
}
