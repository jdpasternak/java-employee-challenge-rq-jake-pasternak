package com.reliaquest.api.gateway;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EmployeeClient {

    public List<Employee> getAll() {
        return null;
    }

    public Employee getById(UUID id) {
        return null;
    }

    public Employee create(CreateEmployeeInput in) {
        return null;
    }

    public boolean deleteByName(String name) {
        return false;
    }
}
