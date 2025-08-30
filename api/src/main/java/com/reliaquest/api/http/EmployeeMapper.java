package com.reliaquest.api.http;

import com.reliaquest.api.model.Employee;

import java.util.Objects;
import java.util.UUID;

public final class EmployeeMapper {
    public static Employee toDomain(WireEmployee wireEmployee) {
        Objects.requireNonNull(wireEmployee.employeeSalary(), "downstream salary null");
        Objects.requireNonNull(wireEmployee.employeeAge(), "downstream age null");
        return new Employee(
                UUID.fromString(wireEmployee.id()),
                wireEmployee.employeeName(),
                wireEmployee.employeeSalary(),
                wireEmployee.employeeAge(),
                wireEmployee.employeeTitle(),
                wireEmployee.employeeEmail()
        );
    }
}
