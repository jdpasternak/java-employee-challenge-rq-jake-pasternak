package com.reliaquest.api.gateway;

import com.reliaquest.api.http.RestClientConfig;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.List;
import java.util.UUID;

@RestClientTest(EmployeeClient.class)
@Import(RestClientConfig.class)
class EmployeeClientTest {

    @Autowired
    EmployeeClient client;

    @Autowired
    MockRestServiceServer server;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void create() {
    }

    @Test
    void deleteByName() {
    }
}