package com.reliaquest.api.controller;

import com.reliaquest.api.model.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
        log.error("Error handling web request.", ex);
        // TODO create response model
        return ResponseEntity.internalServerError().body(Response.error(ex.getMessage()));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolationException(Throwable ex) {
        log.error("ConstraintViolationException!", ex);
        return ResponseEntity.badRequest().build();
    }
}
