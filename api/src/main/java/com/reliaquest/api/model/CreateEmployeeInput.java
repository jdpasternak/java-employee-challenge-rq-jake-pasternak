package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEmployeeInput {
    @NotBlank
    private String name;

    @Min(1)
    private int salary;

    @Min(16) @Max(75)
    private int age;

    @NotBlank
    private String title;
}
