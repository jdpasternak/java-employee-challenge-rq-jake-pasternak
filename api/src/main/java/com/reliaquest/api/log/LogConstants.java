package com.reliaquest.api.log;

public final class LogConstants {
    private LogConstants() {}

    public static final class MDCKeys {
        private MDCKeys() {}

        public static final String CORRELATION_ID = "correlation_id";
    }

    public static final class PropertyKeys {
        private PropertyKeys() {}

        public static final String HTTP_METHOD = "http.method";
        public static final String HTTP_PATH = "http.path";
        public static final String HTTP_STATUS = "http.status";
        public static final String DURATION_MS = "duration_ms";
    }
}
