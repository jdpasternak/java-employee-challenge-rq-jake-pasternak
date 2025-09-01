package com.reliaquest.api.http;

import com.reliaquest.api.exception.BadGatewayException;
import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;

class DownstreamErrorHandlerTest {
    private final DownstreamErrorHandler downstreamErrorHandler = new DownstreamErrorHandler();

    @Test
    void hasError_returnsFalse_for2xx() throws Exception {
        // Given
        var responseOk = new MockClientHttpResponse(new byte[0], HttpStatus.OK);

        // When
        boolean resultOk = downstreamErrorHandler.hasError(responseOk);

        // Then
        Assertions.assertFalse(resultOk);

        responseOk.close();
    }

    @Test
    void hasError_returnsFalse_for204() throws Exception {
        // Given
        var responseNoContent = new MockClientHttpResponse(new byte[0], HttpStatus.NO_CONTENT);

        // When
        boolean resultNoContent = downstreamErrorHandler.hasError(responseNoContent);

        // Then
        Assertions.assertFalse(resultNoContent);

        responseNoContent.close();
    }

    @Test
    void handleError_404_throwsNotFound() {
        // Given
        var id = UUID.randomUUID().toString();
        var resp = new MockClientHttpResponse(new byte[0], HttpStatus.NOT_FOUND);

        // When
        var exception = Assertions.assertThrows(
                EmployeeNotFoundException.class,
                () -> downstreamErrorHandler.handleError(URI.create("/api/v1/employee/%s".formatted(id)), null, resp));

        // Then
        Assertions.assertEquals(exception.getId(), id);
        resp.close();
    }

    @Test
    void handleError_429_parsesRetryAfterSeconds() {
        // Given
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add(HttpHeaders.RETRY_AFTER, "2");

        // When
        var ex = Assertions.assertThrows(
                DownstreamUnavailableException.class,
                () -> downstreamErrorHandler.handleError(URI.create(""), null, response));

        // Then
        Assertions.assertEquals(Duration.ofSeconds(2), ex.getRetryAfter());

        response.close();
    }

    @Test
    void handleError_429_ignoresBadRetryAfter() {
        // Given
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add(HttpHeaders.RETRY_AFTER, "garbage");

        // When
        var ex = Assertions.assertThrows(
                DownstreamUnavailableException.class,
                () -> downstreamErrorHandler.handleError(URI.create(""), null, response));

        // Then
        Assertions.assertNull(ex.getRetryAfter());

        response.close();
    }

    @Test
    void handleError_5xx_throwsDownstreamUnavailable() {
        // Given
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.SERVICE_UNAVAILABLE);

        // When
        Assertions.assertThrows(
                DownstreamUnavailableException.class,
                () -> downstreamErrorHandler.handleError(URI.create(""), null, response));

        // Then

        response.close();
    }

    @Test
    void handleError_other4xx_throwsBadGateway() {
        // Given
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);

        // When

        // Then
        Assertions.assertThrows(
                BadGatewayException.class, () -> downstreamErrorHandler.handleError(URI.create(""), null, response));

        response.close();
    }
}
