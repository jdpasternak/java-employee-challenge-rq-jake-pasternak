package com.reliaquest.api.gateway;

public final class GatewayConstants {
    private GatewayConstants() {}

    public static final String EMPLOYEE_ENDPOINT = "/employee";
    public static final String NAME = "name";

    public static final class DownstreamJSONKeys {
        private DownstreamJSONKeys() {}

        public static final String EMPLOYEE_NAME = "employee_name";
        public static final String EMPLOYEE_SALARY = "employee_salary";
        public static final String EMPLOYEE_AGE = "employee_age";
        public static final String EMPLOYEE_TITLE = "employee_title";
        public static final String EMPLOYEE_EMAIL = "employee_email";
    }
}
