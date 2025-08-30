package com.reliaquest.api.gateway;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface IEmployeeClient {
    List<Employee> getAll();
    Employee getById(@NotNull UUID id);
    Employee create(@Valid @NotNull CreateEmployeeInput input);
    boolean deleteByName(@NotNull String name);
}
