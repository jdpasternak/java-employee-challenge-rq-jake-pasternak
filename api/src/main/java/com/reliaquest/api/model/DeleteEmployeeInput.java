package com.reliaquest.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class DeleteEmployeeInput {
    @NotBlank
    @UUID
    private String id;
}
