package com.reliaquest.api.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

public class DownstreamUnavailableException extends RuntimeException {

    @Getter
    @Setter
    private Duration retryAfter;

    public DownstreamUnavailableException() {
        super("Downstream unavailable!");
    }

    public DownstreamUnavailableException(String message) {
        super(message);
    }

    public DownstreamUnavailableException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

}
