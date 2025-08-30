package com.reliaquest.api.exception;

import com.reliaquest.api.service.EmployeeService;
import lombok.Getter;
import lombok.Setter;

public class EmployeeWithNameAlreadyExistsException extends RuntimeException {

    @Getter
    @Setter
    private String name;
    public EmployeeWithNameAlreadyExistsException() {
        super("An employee with that name already exists");
    }

    public EmployeeWithNameAlreadyExistsException(String employeeName) {
        super("An employee with the name \"%s\" already exists".formatted(employeeName));
        this.name = employeeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
