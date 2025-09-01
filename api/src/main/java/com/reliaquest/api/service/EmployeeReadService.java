package com.reliaquest.api.service;

import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeReadService {

    private final EmployeeClient client;

    @Cacheable(cacheNames = "employees:all", key = "'ALL'")
    public List<Employee> findAll() {
        return client.getAll();
    }
}
