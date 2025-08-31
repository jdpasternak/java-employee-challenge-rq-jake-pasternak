package com.reliaquest.api.controller;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeWithNameAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
        log.error("Error handling web request.", ex);
        // TODO create response model
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class, EmployeeWithNameAlreadyExistsException.class})
    protected ResponseEntity<?> handleInvalidInputExceptions(Throwable ex) {
        log.error("Bad Request!", ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    protected ResponseEntity<?> handleEmployeeNotFoundException(Throwable ex) {
        log.error("EmployeeNotFoundException!", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<?> handleNoResourceFoundException(Throwable ex) {
        log.error("NoResourceFoundException!", ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
