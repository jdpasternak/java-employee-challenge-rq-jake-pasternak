package com.reliaquest.api.exception;

public class ValidationException extends Exception {
    public ValidationException() {
        super("Invalid input!");
    }

    public ValidationException(String message) {
        super(message);
    }
}
