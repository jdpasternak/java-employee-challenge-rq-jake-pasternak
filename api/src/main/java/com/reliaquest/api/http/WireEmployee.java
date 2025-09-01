package com.reliaquest.api.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.reliaquest.api.gateway.GatewayConstants.DownstreamJSONKeys.*;


public record WireEmployee(
        String id,
        @JsonProperty(EMPLOYEE_NAME) String employeeName,
        @JsonProperty(EMPLOYEE_SALARY) Integer employeeSalary,
        @JsonProperty(EMPLOYEE_AGE) Integer employeeAge,
        @JsonProperty(EMPLOYEE_TITLE) String employeeTitle,
        @JsonProperty(EMPLOYEE_EMAIL) String employeeEmail) {
}
