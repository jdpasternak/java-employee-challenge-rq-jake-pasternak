package com.reliaquest.api.service;

import static com.reliaquest.api.cache.CacheNames.EMPLOYEES_ALL;

import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.Employee;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeReadService {

    private final EmployeeClient client;

    @Cacheable(cacheNames = EMPLOYEES_ALL)
    public List<Employee> findAll() {
        return client.getAll();
    }
}
