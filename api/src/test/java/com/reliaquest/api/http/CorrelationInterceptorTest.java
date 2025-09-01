package com.reliaquest.api.http;

import com.reliaquest.api.config.PropsConfig;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.UUID;

import static com.reliaquest.api.http.HttpConstants.Headers.X_CORRELATION_ID;
import static com.reliaquest.api.log.LogConstants.MDCKeys.CORRELATION_ID;

@RestClientTest(EmployeeClient.class)
@Import({RestClientConfig.class, DownstreamErrorHandler.class, CorrelationInterceptor.class, PropsConfig.class})
class CorrelationInterceptorTest {

    @Autowired
    EmployeeClient client;

    @Autowired
    MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Test
    void intercept_sendsCorrelationIdDownstream() {
        // Given
        MDC.put(CORRELATION_ID, "abc123");

        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header(X_CORRELATION_ID, "abc123"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        """
                                {"data": [{"id":"%s", "employee_name":"N","employee_salary":1,"employee_age":20,"employee_title":"T","employee_email":"e@c"}],"status":"ok"}"""
                                .formatted(UUID.randomUUID()),
                        MediaType.APPLICATION_JSON));

        // When
        var result = client.getAll();

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("N", result.get(0).getName());
    }

    @Test
    void intercept_whenPost_noMutatedPayload() {
        // Given
        MDC.put(CORRELATION_ID, "def456");
        var id = UUID.randomUUID();
        var employeeToCreate = new CreateEmployeeInput("N", 1, 20, "T");
        var expectedBody =
                """
                        {
                            "name": "N",
                            "salary": 1,
                            "age": 20,
                            "title": "T"
                        }
                        """;

        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockRestRequestMatchers.content().json(expectedBody))
                .andExpect(MockRestRequestMatchers.header(X_CORRELATION_ID, "def456"))
                .andRespond(MockRestResponseCreators.withSuccess(
                        """
                                {"data": {"id":"%s", "employee_name":"N","employee_salary":1,"employee_age":20,"employee_title":"T","employee_email":"e@c"},
                                "status":"ok"}"""
                                .formatted(id),
                        MediaType.APPLICATION_JSON));

        // When
        var result = client.create(employeeToCreate);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("N", result.getName());
        Assertions.assertEquals(1, result.getSalary());
        Assertions.assertEquals(20, result.getAge());
        Assertions.assertEquals("T", result.getTitle());
    }
}
