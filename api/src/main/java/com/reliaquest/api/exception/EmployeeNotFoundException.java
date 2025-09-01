package com.reliaquest.api.exception;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {

    private String id;

    public EmployeeNotFoundException() {
        super("Employee not found!");
    }

    public EmployeeNotFoundException(String id) {
        this();
        this.id = id;
    }
}
