package com.reliaquest.api.exception;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownstreamUnavailableException extends RuntimeException {

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
