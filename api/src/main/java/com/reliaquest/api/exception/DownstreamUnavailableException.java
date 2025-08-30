package com.reliaquest.api.exception;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

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
