package com.reliaquest.api.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class CorrelationFilter extends OncePerRequestFilter {
    static final String HDR = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String correlationId = Optional.ofNullable(request.getHeader(HDR))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        response.setHeader(HDR, correlationId);
        MDC.put("correlation_id", correlationId);

        log.atDebug()
                .addKeyValue("http.method", request.getMethod())
                .addKeyValue("http.path", request.getRequestURI())
                .log("request_received");

        long t0 = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            log.atInfo()
                    .addKeyValue("http.method", request.getMethod())
                    .addKeyValue("http.path", request.getRequestURI())
                    .addKeyValue("http.status", response.getStatus())
                    .addKeyValue("duration_ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0))
                    .log("request_completed");
            MDC.clear();
        }
    }
}
