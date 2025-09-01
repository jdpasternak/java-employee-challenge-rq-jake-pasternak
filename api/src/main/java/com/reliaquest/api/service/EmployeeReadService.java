package com.reliaquest.api.service;

import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.reliaquest.api.cache.CacheConstants.EMPLOYEES_ALL;

@Component
@RequiredArgsConstructor
public class EmployeeReadService {

    private final EmployeeClient client;

    @Cacheable(cacheNames = EMPLOYEES_ALL)
    public List<Employee> findAll() {
        return client.getAll();
    }
}
