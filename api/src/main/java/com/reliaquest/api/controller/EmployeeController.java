package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {
    @Override
    @GetMapping
    public ResponseEntity<Response<List>> getAllEmployees() {
        return ResponseEntity.ok(Response.handledWith(List.of("Employee list")));
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<Response<List>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.ok(Response.handledWith(List.of(searchString)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Response<Employee>> getEmployeeById(@PathVariable String id) {
        Employee employee = new Employee();
        employee.setId(id);
        return ResponseEntity.ok(Response.handledWith(employee));
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Response<Integer>> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(Response.handledWith(1));
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<Response<List<String>>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(Response.handledWith(List.of("Name1")));
    }

    @Override
    @PostMapping()
    public ResponseEntity<Response<Employee>> createEmployee(Object employeeInput) { // TODO map this object to CreateEmployeeInput
        return ResponseEntity.ok(Response.handledWith(new Employee()));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(Response.handledWith(id));
    }
}
