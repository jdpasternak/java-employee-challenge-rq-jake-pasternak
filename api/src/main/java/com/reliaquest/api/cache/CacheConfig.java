package com.reliaquest.api.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Bean
    CacheManager cacheManager(CacheProperties cacheProperties) {
        var manager = new CaffeineCacheManager(CacheNames.EMPLOYEES_ALL);
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(cacheProperties.getMaxSize())
                .expireAfterWrite(cacheProperties.getTtl()));
        return manager;
    }
}
