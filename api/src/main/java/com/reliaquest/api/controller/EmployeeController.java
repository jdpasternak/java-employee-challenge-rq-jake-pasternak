package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.base-path:/api/v1}/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        var employees = employeeService.findAll();
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(employees);
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        var matchedEmployees = employeeService.search(searchString);
        if (matchedEmployees.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(matchedEmployees);
        }
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        var highestSalary = employeeService.findHighestSalaryOfEmployees();
        if (highestSalary.isPresent()) {
            return ResponseEntity.ok(highestSalary.getAsInt());
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        var employeeNames = employeeService.findTopTenHighestEarningEmployees();
        if (employeeNames.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeInput employeeInput) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}
