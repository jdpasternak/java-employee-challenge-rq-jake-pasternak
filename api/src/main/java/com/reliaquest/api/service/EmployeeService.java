package com.reliaquest.api.service;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    EmployeeClient client;

    public List<Employee> findAll() {
        return null;
    }

    public List<Employee> search(String fragment) throws ValidationException {
        return null;
    }

    public Employee findById(String id) throws EmployeeNotFoundException {
        return null;
    }

    public Optional<Integer> findHighestSalaryOfEmployees() {
        return null;
    }

    public List<Employee> findTopTenHighestEarningEmployees() {
        return null;
    }

    public Employee createEmployee(CreateEmployeeInput employeeInput) throws ValidationException {
        return null;
    }

    public boolean deleteEmployeeById(String id) throws EmployeeNotFoundException {
        return false;
    }
}
