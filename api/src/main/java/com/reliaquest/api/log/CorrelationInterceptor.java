package com.reliaquest.api.log;

import static com.reliaquest.api.http.HttpConstants.Headers.X_CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.MDCKeys.CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.PropertyKeys.DURATION_MS;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CorrelationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String correlationId = MDC.get(CORRELATION_ID);
        if (correlationId != null) request.getHeaders().set(X_CORRELATION_ID, correlationId);
        var t0 = System.nanoTime();

        var method = request.getMethod().name();
        var path = request.getURI().getPath();

        log.atDebug()
                .addKeyValue("downstream.method", method)
                .addKeyValue("downstream.path", path)
                .log("downstream_request_sent");

        var response = execution.execute(request, body);
        log.atInfo()
                .addKeyValue("downstream.method", method)
                .addKeyValue("downstream.path", path)
                .addKeyValue("downstream.status", response.getStatusCode())
                .addKeyValue(DURATION_MS, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0))
                .log("downstream_request_completed");

        return response;
    }
}
