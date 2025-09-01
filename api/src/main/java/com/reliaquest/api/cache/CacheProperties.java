package com.reliaquest.api.cache;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.cache.employees")
@Validated
@Data
public class CacheProperties {
    @NotNull @DurationUnit(ChronoUnit.SECONDS)
    private Duration ttl = Duration.ofSeconds(30);

    @Min(1)
    private int maxSize = 1;
}
