package com.reliaquest.api.service;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.helper.EmployeeComparators;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class EmployeeService {

    private final EmployeeClient client;

    public EmployeeService(EmployeeClient client) {
        this.client = client;
    }

    public List<Employee> findAll() {
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

    public Employee findById(@NotNull UUID id) {
        return client.getById(id);
    }

    public OptionalInt findHighestSalaryOfEmployees() {
        List<Employee> employees = client.getAll();
        return employees.stream().mapToInt(Employee::getSalary).max();
    }

    public List<Employee> findTopTenHighestEarningEmployees() {
        List<Employee> employees = client.getAll();
        return employees.stream()
                .sorted(EmployeeComparators.BY_SALARY_DESC_NAME_ASC_ID_ASC)
                .limit(10)
                .toList();
    }

    public Employee createEmployee(@Valid CreateEmployeeInput employeeInput) throws ValidationException {
        return client.create(employeeInput);
    }

    public String deleteEmployeeById(@NotNull UUID id) throws EmployeeNotFoundException {
        Employee employeeFound = client.getById(id);
        String name = employeeFound.getName();
        boolean deleted = client.deleteByName(name);
        if (!deleted) {
            throw new EmployeeNotFoundException();
        }
        return name;
    }
}
