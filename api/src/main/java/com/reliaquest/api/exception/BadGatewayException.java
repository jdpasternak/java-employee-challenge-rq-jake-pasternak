package com.reliaquest.api.exception;

public class BadGatewayException extends RuntimeException {
    private Exception cause;

    public BadGatewayException() {
        super("bad gateway!");
    }

    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Exception cause) {
        super(message);
        this.cause = cause;
    }
}
