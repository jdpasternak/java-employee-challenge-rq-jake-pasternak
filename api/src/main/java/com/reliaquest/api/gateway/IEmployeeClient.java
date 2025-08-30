package com.reliaquest.api.gateway;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;

import java.util.List;
import java.util.UUID;

public interface IEmployeeClient {
    List<Employee> getAll();
    Employee getById(UUID id);
    Employee create(CreateEmployeeInput input);
    boolean deleteByName(String name);
}
