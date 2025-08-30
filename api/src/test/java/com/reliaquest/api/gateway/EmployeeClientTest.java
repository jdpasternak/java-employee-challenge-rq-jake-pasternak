package com.reliaquest.api.gateway;

import com.reliaquest.api.exception.BadGatewayException;
import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.http.RestClientConfig;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;


@RestClientTest(EmployeeClient.class)
@Import({RestClientConfig.class, EmployeeClientTest.MethodValidationConfiguration.class})
class EmployeeClientTest {

    @Autowired
    IEmployeeClient client;

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

        server.verify();
        server.reset();
    }

    @Test
    void getAll_whenNoEmployeesExist_returnsEmptyList() {
        // Given
        String body = """
                { "data": [],
                  "status":"Successfully processed request."
                }""";

        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        List<Employee> employees = client.getAll();

        // Then
        Assertions.assertNotNull(employees);
        Assertions.assertTrue(employees.isEmpty());

        server.verify();
        server.reset();
    }

    @Test
    void getAll_whenServerError_throwsDownstreamUnavailableException() {
        // Given
        String body = """
                { "data": [],
                  "status":"Successfully processed request."
                }""";

        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // When
        Assertions.assertThrows(DownstreamUnavailableException.class, () -> client.getAll());

        // Then
        server.verify();
        server.reset();
    }

    @Test
    void getAll_whenTooManyRequest_throwsDownstreamUnavailableException() {
        // Given
        String body = """
                { "data": [],
                  "status":"Successfully processed request."
                }""";
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, "10");
        server.expect(MockRestRequestMatchers.requestTo("/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.TOO_MANY_REQUESTS).headers(headers));

        // When
        Assertions.assertThrows(DownstreamUnavailableException.class, () -> client.getAll());

        // Then
        server.verify();
        server.reset();
    }

    @Test
    void getById_whenNotFound_throwsEmployeeNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();
        server.expect(MockRestRequestMatchers.requestTo("/employee/" + id))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        // When
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> client.getById(id));

        // Then
        server.verify();
        server.reset();
    }

    @Test
    void getById_whenEmployeeWithIdExists_returnsEmployee() {
        // Given
        String idToFind = "11111111-1111-1111-1111-111111111111";
        UUID uuidToFind = UUID.fromString(idToFind);
        String body = """
                { "data": {
                    "id":"%s",
                    "employee_name":"Bill Bob",
                    "employee_salary":89750,
                    "employee_age":24,
                    "employee_title":"Documentation Engineer",
                    "employee_email":"billBob@company.com"
                  },
                  "status":"Successfully processed request."
                }""".formatted("11111111-1111-1111-1111-111111111111");
        server.expect(MockRestRequestMatchers.requestTo("/employee/" + idToFind))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        Employee result = client.getById(uuidToFind);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertAll(
                () -> Assertions.assertEquals(uuidToFind, result.getId()),
                () -> Assertions.assertEquals("Bill Bob", result.getName()),
                () -> Assertions.assertEquals(89750, result.getSalary()),
                () -> Assertions.assertEquals(24, result.getAge()),
                () -> Assertions.assertEquals("Documentation Engineer", result.getTitle()),
                () -> Assertions.assertEquals("billBob@company.com", result.getEmail())
        );

        server.verify();
        server.reset();
    }

    @Test
    void getById_whenTooManyRequest_throwsDownstreamUnavailableException() {
        // Given
        String idToFind = "11111111-1111-1111-1111-111111111111";
        UUID uuidToFind = UUID.fromString(idToFind);
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, "10");
        server.expect(MockRestRequestMatchers.requestTo("/employee/" + idToFind))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.TOO_MANY_REQUESTS).headers(headers));

        // When
        var exception = Assertions.assertThrows(DownstreamUnavailableException.class, () -> client.getById(uuidToFind));

        // Then
        Assertions.assertEquals(Duration.ofSeconds(10), exception.getRetryAfter());

        server.verify();
        server.reset();
    }

    @Nested
    class CreateTests {
        @Test
        void create_whenInvalidInput_throwsConstraintValidationException() {
            // Given
            var employeeInput1 = new CreateEmployeeInput("", 1, 20, "T");
            var employeeInput2 = new CreateEmployeeInput("N", null, 20, "T");
            var employeeInput3 = new CreateEmployeeInput("N", 1, null, "T");
            var employeeInput4 = new CreateEmployeeInput("N", 1, 20, "");

            // When
            Assertions.assertAll(
                    () -> Assertions.assertThrows(ConstraintViolationException.class, () -> client.create(employeeInput1)),
                    () -> Assertions.assertThrows(ConstraintViolationException.class, () -> client.create(employeeInput2)),
                    () -> Assertions.assertThrows(ConstraintViolationException.class, () -> client.create(employeeInput3)),
                    () -> Assertions.assertThrows(ConstraintViolationException.class, () -> client.create(employeeInput4)));

            // Then
            server.verify();
            server.reset();
        }


        @Test
        void create_whenValidInput_returnsCreatedEmployee() {
            // Given
            var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");
            var expectedBody = """
                { "data": {
                    "id":"11111111-1111-1111-1111-111111111111",
                    "employee_name":"N",
                    "employee_salary":1,
                    "employee_age":20,
                    "employee_title":"T",
                    "employee_email":"test@company.com"
                  },
                  "status":"Successfully processed request."
                }""";

            server.expect(MockRestRequestMatchers.requestTo("/employee"))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                    .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockRestRequestMatchers.content().json("""
                        {"name":"N","salary":1,"age":20,"title":"T"}
                        """))
                    .andRespond(MockRestResponseCreators.withSuccess(expectedBody, MediaType.APPLICATION_JSON));

            // When
            var result = client.create(employeeInput);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertAll(
                    () -> Assertions.assertEquals("N", result.getName()),
                    () -> Assertions.assertEquals(1, result.getSalary()),
                    () -> Assertions.assertEquals(20, result.getAge()),
                    () -> Assertions.assertEquals("T", result.getTitle()),
                    () -> Assertions.assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                            result.getId()),
                    () -> Assertions.assertEquals("test@company.com", result.getEmail())
            );

            server.verify();
            server.reset();
        }

        @Test
        void create_whenServerError_throwsDownstreamUnavailableException() {
            // Given
            var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");

            server.expect(MockRestRequestMatchers.requestTo("/employee"))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                    .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockRestRequestMatchers.content().json("""
                        {"name":"N","salary":1,"age":20,"title":"T"}
                        """))
                    .andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

            // When
            Assertions.assertThrows(DownstreamUnavailableException.class, () -> client.create(employeeInput));

            // Then
            server.verify();
            server.reset();
        }

        @Test
        void create_whenBadRequest_throwsBadGatewayException() {
            // Given
            var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");

            server.expect(MockRestRequestMatchers.requestTo("/employee"))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                    .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockRestRequestMatchers.content().json("""
                        {"name":"N","salary":1,"age":20,"title":"T"}
                        """))
                    .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

            // When
            Assertions.assertThrows(BadGatewayException.class, () -> client.create(employeeInput));

            // Then
            server.verify();
            server.reset();
        }


        @Test
        void create_whenTooManyRequests_noRetry() {
            // Given
            var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");
            var httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.RETRY_AFTER, "10");

            server.expect(MockRestRequestMatchers.requestTo("/employee"))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                    .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockRestRequestMatchers.content().json("""
                        {"name":"N","salary":1,"age":20,"title":"T"}
                        """))
                    .andRespond(MockRestResponseCreators.withStatus(HttpStatus.TOO_MANY_REQUESTS).headers(httpHeaders));

            // When
            var exception = Assertions.assertThrows(DownstreamUnavailableException.class, () -> client.create(employeeInput));

            // Then
            Assertions.assertEquals(Duration.ofSeconds(10), exception.getRetryAfter());
            server.verify();
            server.reset();
        }
    }


    @Nested
    class DeleteByNameTests {
        @Test
        void deleteByName_whenNoEmployeeExists_returnsFalse() {
            // Given
            String nameToDelete = "N";
            String expectedBody = """
                    { "data": false,
                      "status":"Successfully processed request."
                    }""";

            server.expect(MockRestRequestMatchers.requestTo("/employee"))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
                    .andExpect(MockRestRequestMatchers.content().json("""
                            {"name":"%s"}
                            """.formatted(nameToDelete)))
                    .andRespond(MockRestResponseCreators.withSuccess(expectedBody, MediaType.APPLICATION_JSON));

            // When
            Assertions.assertThrows(EmployeeNotFoundException.class, () -> client.deleteByName(nameToDelete));

            // Then
            server.verify();
            server.reset();
        }
    }

    @TestConfiguration
    static class MethodValidationConfiguration {
        @Bean
        static MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }
}