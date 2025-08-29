package com.reliaquest.api.service;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Validated
public class EmployeeService {

    @Autowired
    EmployeeClient client;

    public List<Employee> findAll() throws DownstreamUnavailableException {
        return client.getAll();
    }

    public List<Employee> search(@Valid SearchInput searchInput) {
        List<Employee> employees = client.getAll();
        String normalizedSearchString = searchInput.getSearchString().toLowerCase(Locale.ROOT);
        return employees.stream().filter(employee ->
                        employee.getName().toLowerCase(Locale.ROOT)
                                .contains(normalizedSearchString))
                .toList();
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
