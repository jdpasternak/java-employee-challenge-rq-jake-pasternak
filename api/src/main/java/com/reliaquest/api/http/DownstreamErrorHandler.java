package com.reliaquest.api.http;

import com.reliaquest.api.exception.BadGatewayException;
import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

public class DownstreamErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        this.handleError(URI.create("about:blank"), null, response);
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();

        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            var id = parseIdFromURL(url.toString());
            throw new EmployeeNotFoundException(id);
        }

        if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            Duration retryAfter = parseRetryAfter(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
            throw new DownstreamUnavailableException("rate limited", retryAfter);
        }

        if (status.is5xxServerError()) {
            throw new DownstreamUnavailableException("downstream 5xx");
        }

        if (status.is4xxClientError()) {
            throw new BadGatewayException("unexpected 4xx from downstream: " + status.value());
        }
    }

    private Duration parseRetryAfter(String value) {
        try {
            if (value == null) return null;
            if (value.chars().allMatch(Character::isDigit)) {
                return Duration.ofSeconds(Long.parseLong(value));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String parseIdFromURL(String url) {
        var lastSlash = url.lastIndexOf("/");
        return (lastSlash >= 0 && lastSlash < url.length() - 1) ? url.substring(lastSlash + 1) : url;
    }
}
