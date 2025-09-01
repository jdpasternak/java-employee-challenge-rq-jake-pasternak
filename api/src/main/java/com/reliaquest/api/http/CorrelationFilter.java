package com.reliaquest.api.http;

import static com.reliaquest.api.http.HttpConstants.Headers.X_CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.MDCKeys.CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.PropertyKeys.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class CorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String correlationId = Optional.ofNullable(request.getHeader(X_CORRELATION_ID))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        response.setHeader(X_CORRELATION_ID, correlationId);
        MDC.put(CORRELATION_ID, correlationId);

        log.atDebug()
                .addKeyValue(HTTP_METHOD, request.getMethod())
                .addKeyValue(HTTP_PATH, request.getRequestURI())
                .log("request_received");

        long t0 = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            log.atInfo()
                    .addKeyValue(HTTP_METHOD, request.getMethod())
                    .addKeyValue(HTTP_PATH, request.getRequestURI())
                    .addKeyValue(HTTP_STATUS, response.getStatus())
                    .addKeyValue(DURATION_MS, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0))
                    .log("request_completed");
            MDC.clear();
        }
    }
}
