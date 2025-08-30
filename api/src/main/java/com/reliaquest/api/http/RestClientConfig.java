package com.reliaquest.api.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    RestTemplate employeeRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri("http://localhost:8112/api/v1")
                .build();
    }
}
