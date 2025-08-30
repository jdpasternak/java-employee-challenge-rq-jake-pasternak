package com.reliaquest.api.exception;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException() {
        super("bad gateway!");
    }

    public BadGatewayException(String message) {
        super(message);
    }
}
