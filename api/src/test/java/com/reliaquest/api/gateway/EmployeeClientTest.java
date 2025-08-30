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
    void getAll_whenEmployeesExist_returnsListOfEmployees() {
        // Given
        String body = """
                { "data": [
                      {
                        "id":"%s",
                        "employee_name":"Bill Bob",
                        "employee_salary":89750,
                        "employee_age":24,
                        "employee_title":"Documentation Engineer",
                        "employee_email":"billBob@company.com"
                      },
                      {
                        "id":"%s",
                        "employee_name":"Sally Sue",
                        "employee_salary":12345,
                        "employee_age":28,
                        "employee_title":"Documentation Manager",
                        "employee_email":"sallysue@company.com"
                      }
                  ],
                  "status":"Successfully processed request."
                }""".formatted(UUID.randomUUID(), UUID.randomUUID());

        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        List<Employee> employees = client.getAll();

        // Then
        Assertions.assertNotNull(employees);
        Assertions.assertFalse(employees.isEmpty());
        Assertions.assertEquals(2, employees.size());
        Assertions.assertTrue(employees.stream().map(Employee::getName).toList()
                .containsAll(List.of("Bill Bob", "Sally Sue")));
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