package com.reliaquest.api.http;

import com.reliaquest.api.config.EmployeeApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(EmployeeApiProperties.class)
public class RestClientConfig {

    @Bean
    RestTemplate employeeRestTemplate(RestTemplateBuilder builder,
                                      EmployeeApiProperties properties,
                                      DownstreamErrorHandler downstreamErrorHandler,
                                      CorrelationInterceptor correlationInterceptor) {
        return builder.rootUri(properties.getBaseUrl())
                .setConnectTimeout(properties.getTimeouts().getConnect())
                .setReadTimeout(properties.getTimeouts().getRead())
                .additionalInterceptors(correlationInterceptor)
                .errorHandler(downstreamErrorHandler)
                .build();
    }
}
