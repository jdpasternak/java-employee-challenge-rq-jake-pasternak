package com.reliaquest.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.downstream.employee")
@Validated
@Data
public class EmployeeApiProperties {
    @NotBlank
    private String baseUrl;

    @NotNull private Timeouts timeouts = new Timeouts();

    @Data
    public static class Timeouts {
        @NotNull private Duration connect = Duration.ofSeconds(1);

        @NotNull private Duration read = Duration.ofSeconds(3);
    }
}
