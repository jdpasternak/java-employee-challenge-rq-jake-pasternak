package com.reliaquest.api.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WireEmployee(
        String id,
        @JsonProperty("employee_name") String employeeName,
        @JsonProperty("employee_salary") Integer employeeSalary,
        @JsonProperty("employee_age") Integer employeeAge,
        @JsonProperty("employee_title") String employeeTitle,
        @JsonProperty("employee_email") String employeeEmail) {}
