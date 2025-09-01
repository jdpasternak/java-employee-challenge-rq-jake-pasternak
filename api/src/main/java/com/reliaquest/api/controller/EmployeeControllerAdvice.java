package com.reliaquest.api.controller;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeWithNameAlreadyExistsException;
import com.reliaquest.api.http.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;

import static com.reliaquest.api.http.ErrorCode.*;
import static com.reliaquest.api.log.LogConstants.MDCKeys.CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.PropertyKeys.*;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable exception, HttpServletRequest request) {
        log.atError()
                .addKeyValue(HTTP_METHOD, request.getMethod())
                .addKeyValue(HTTP_PATH, request.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value())
                .addKeyValue("error.code", SERVER_ERROR).log("server_error", exception);

        var problemDetail = base(HttpStatus.INTERNAL_SERVER_ERROR, request, "Server Error", SERVER_ERROR);
        problemDetail.setDetail("Server error.");

        return ResponseEntity.internalServerError().body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        var errors = exception.getConstraintViolations().stream().map(violation -> Map.of(
                "field", leafName(violation.getPropertyPath().toString()),
                "message", violation.getMessage()
        )).sorted(Comparator.comparing(map -> map.get("field"))).toList();

        var problemDetail = base(HttpStatus.BAD_REQUEST, request, "Constraint Violation", VALIDATION_FAILED);
        problemDetail.setDetail("One or more constraints failed.");
        problemDetail.setProperty("errors", errors);

        log.atInfo()
                .addKeyValue(HTTP_METHOD, request.getMethod())
                .addKeyValue(HTTP_PATH, request.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.BAD_REQUEST.value())
                .addKeyValue("error.code", VALIDATION_FAILED)
                .addKeyValue("violations.count", errors.size())
                .addKeyValue("violations.fields", errors.stream().map(m -> m.get("field")).toList())
                .log("validation_failed");

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    ResponseEntity<ProblemDetail> handleEmployeeNotFoundException(
            EmployeeNotFoundException ex, HttpServletRequest req) {

        var pd = base(HttpStatus.NOT_FOUND, req, "Employee Not Found", EMPLOYEE_NOT_FOUND);
        pd.setDetail("An employee with ID '%s' doesn't exists.".formatted(ex.getId()));

        log.atWarn()
                .addKeyValue(HTTP_METHOD, req.getMethod())
                .addKeyValue(HTTP_PATH, req.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.NOT_FOUND.value())
                .addKeyValue("error.code", EMPLOYEE_NOT_FOUND)
                .addKeyValue("id", ex.getId())
                .log("not_found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(EmployeeWithNameAlreadyExistsException.class)
    ResponseEntity<ProblemDetail> handleEmployeeWithNameAlreadyExistsException(
            EmployeeWithNameAlreadyExistsException ex, HttpServletRequest req) {

        var pd = base(HttpStatus.CONFLICT, req, "Conflict", EMPLOYEE_NAME_CONFLICT);
        pd.setDetail("An employee named '%s' already exists.".formatted(ex.getName()));

        log.atWarn()
                .addKeyValue(HTTP_METHOD, req.getMethod())
                .addKeyValue(HTTP_PATH, req.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.CONFLICT.value())
                .addKeyValue("error.code", EMPLOYEE_NAME_CONFLICT)
                .addKeyValue("name", ex.getName())
                .log("conflict");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(DownstreamUnavailableException.class)
    protected ResponseEntity<?> handleDownstreamUnavailableException(DownstreamUnavailableException exception, HttpServletRequest request) {
        var pd = base(HttpStatus.SERVICE_UNAVAILABLE, request, "Downstream Unavailable", DOWNSTREAM_UNAVAILABLE);
        pd.setDetail("The downstream server has responded with an error.");

        log.atWarn()
                .addKeyValue(HTTP_METHOD, request.getMethod())
                .addKeyValue(HTTP_PATH, request.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.CONFLICT.value())
                .addKeyValue("error.code", DOWNSTREAM_UNAVAILABLE)
                .log("downstream_unavailable");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(pd);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request) {
        log.atInfo()
                .addKeyValue(HTTP_METHOD, request.getMethod())
                .addKeyValue(HTTP_PATH, request.getRequestURI())
                .addKeyValue(HTTP_STATUS, HttpStatus.METHOD_NOT_ALLOWED.value())
                .log("no_resource_found");
        exception.getBody().setProperty(CORRELATION_ID, MDC.get(CORRELATION_ID));
        exception.getBody().setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exception.getBody());
    }

    private static ProblemDetail base(HttpStatus status, HttpServletRequest req, String title, ErrorCode code) {
        var pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty(CORRELATION_ID, MDC.get(CORRELATION_ID));
        pd.setProperty("error.code", code);
        return pd;
    }

    private static String leafName(String path) {
        int dot = path.lastIndexOf('.');
        return (dot >= 0 && dot < path.length() - 1) ? path.substring(dot + 1) : path;
    }
}
