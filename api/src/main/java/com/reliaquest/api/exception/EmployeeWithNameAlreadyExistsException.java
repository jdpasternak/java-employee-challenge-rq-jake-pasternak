package com.reliaquest.api.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeWithNameAlreadyExistsException extends RuntimeException {

    private String name;

    public EmployeeWithNameAlreadyExistsException() {
        super("An employee with that name already exists");
    }

    public EmployeeWithNameAlreadyExistsException(String employeeName) {
        super("An employee with the name \"%s\" already exists".formatted(employeeName));
        this.name = employeeName;
    }
}
