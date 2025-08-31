package com.reliaquest.api.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
