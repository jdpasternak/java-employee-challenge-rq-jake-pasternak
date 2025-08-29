package com.reliaquest.api.gateway;

import com.reliaquest.api.model.Employee;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeClient {

    public List<Employee> getAll() {
        return null;
    }

    public Employee getById(String id) {
        return null;
    }

    public Employee create(Employee in) {
        return null;
    }

    public boolean deleteByName(String name) {
        return false;
    }
}
