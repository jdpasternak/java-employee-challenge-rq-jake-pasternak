package com.reliaquest.api.service;

import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class EmployeeReadServiceTest {

    @MockBean
    EmployeeClient client;
    @Autowired
    EmployeeReadService readService;

    @Test
    void findAll_cached_between_calls() {
        var list = List.of(new Employee(UUID.randomUUID(), "A", 1, 20, "T", "e@c"));
        Mockito.when(client.getAll()).thenReturn(list);

        var r1 = readService.findAll();
        var r2 = readService.findAll();

        Assertions.assertSame(r1, r2);
        Mockito.verify(client, Mockito.times(1)).getAll();
    }
}