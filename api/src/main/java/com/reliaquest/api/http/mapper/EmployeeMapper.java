package com.reliaquest.api.http.mapper;

import com.reliaquest.api.exception.BadGatewayException;
import com.reliaquest.api.http.WireCreateEmployee;
import com.reliaquest.api.http.WireEmployee;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.UUID;

public final class EmployeeMapper {
    public static Employee toDomain(WireEmployee wireEmployee) {
        if (wireEmployee == null) throw new BadGatewayException("downstream returned null employee");

        final UUID id = parseUuid(wireEmployee.id());

        final Integer salary = wireEmployee.employeeSalary();
        if (salary == null) {
            throw new BadGatewayException("downstream employee_salary missing");
        }
        if (salary <= 0) {
            throw new BadGatewayException("downstream employee_salary <= 0");
        }

        final Integer age = wireEmployee.employeeAge();
        if (age == null) {
            throw new BadGatewayException("downstream employee_age missing");
        }
        if (age < 16 || age > 75) {
            throw new BadGatewayException("downstream employee_age out of range: " + age);
        }

        return new Employee(
                UUID.fromString(wireEmployee.id()),
                wireEmployee.employeeName(),
                wireEmployee.employeeSalary(),
                wireEmployee.employeeAge(),
                wireEmployee.employeeTitle(),
                wireEmployee.employeeEmail());
    }

    public static WireCreateEmployee toWire(CreateEmployeeInput createEmployeeInput) {
        return new WireCreateEmployee(
                createEmployeeInput.name(),
                createEmployeeInput.salary(),
                createEmployeeInput.age(),
                createEmployeeInput.title());
    }

    private static UUID parseUuid(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (RuntimeException runtimeException) {
            throw new BadGatewayException("downstream employee id is not a UUID: " + uuidString, runtimeException);
        }
    }
}
