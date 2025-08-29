package com.reliaquest.api.exception;

public class DownstreamUnavailableException extends RuntimeException {

    public DownstreamUnavailableException() {
        super("Downstream unavailable!");
    }

    public DownstreamUnavailableException(String message) {
        super(message);
    }
}
