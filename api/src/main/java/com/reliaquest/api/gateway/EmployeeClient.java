package com.reliaquest.api.gateway;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmployeeClient {

    public List<Employee> getAll() {
        return null;
    }

    public Employee getById(String id) {
        return null;
    }

    public Employee create(CreateEmployeeInput in) {
        return null;
    }

    public boolean deleteByName(String name) {
        return false;
    }
}
