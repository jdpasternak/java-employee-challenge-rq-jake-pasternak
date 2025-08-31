package com.reliaquest.api.controller;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeWithNameAlreadyExistsException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = EmployeeController.class)
@Import(EmployeeControllerAdvice.class)
class EmployeeControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    EmployeeService service;

    @Test
    void getAllEmployees_whenNoEmployeesExist_returnsNoContent() throws Exception {
        // Given
        var employees = new ArrayList<Employee>();
        Mockito.when(service.findAll()).thenReturn(employees);

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getAllEmployees_whenEmployeesExist_returnsListOfEmployees() throws Exception {
        // Given
        var employees = List.of(
                new Employee(UUID.randomUUID(), "N1", 1, 20, "T1", "e1@c"),
                new Employee(UUID.randomUUID(), "N2", 1, 20, "T2", "e2@c"),
                new Employee(UUID.randomUUID(), "N3", 1, 20, "T3", "e3@c"),
                new Employee(UUID.randomUUID(), "N4", 1, 20, "T4", "e4@c"),
                new Employee(UUID.randomUUID(), "N5", 1, 20, "T5", "e5@c"));
        Mockito.when(service.findAll()).thenReturn(employees);

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("N1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value("20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("T1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("e1@c"));

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getAllEmployees_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
        // Given
        Mockito.when(service.findAll()).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeesByNameSearch_whenNoMatchingNameExists_returnsStatusNoContent() throws Exception {
        // Given
        var searchInput = new SearchInput("bob");
        Mockito.when(service.search(searchInput)).thenReturn(new ArrayList<>());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/search/%s".formatted(searchInput.getSearchString())))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verify(service).search(searchInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeesByNameSearch_whenMatchingNameExists_returnsEmployee() throws Exception {
        // Given
        var searchInput = new SearchInput("bob");
        UUID idB = UUID.randomUUID();
        UUID idJ = UUID.randomUUID();
        Mockito.when(service.search(searchInput))
                .thenReturn(List.of(
                        new Employee(idB, "Bob Bobster", 1, 20, "T", "eb@c"),
                        new Employee(idJ, "Jane Bobster", 1, 20, "T", "ej@c")));
        var expectedBody =
                """
                [
                    {
                        "id":"%s",
                        "name":"Bob Bobster",
                        "salary":1,
                        "age":20,
                        "title":"T",
                        "email":"eb@c"
                    },
                    {
                        "id":"%s",
                        "name":"Jane Bobster",
                        "salary":1,
                        "age":20,
                        "title":"T",
                        "email":"ej@c"
                    }
                ]
                """
                        .formatted(idB, idJ);

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/search/%s".formatted(searchInput.getSearchString())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedBody));

        // Then
        Mockito.verify(service).search(searchInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeesByNameSearch_whenServiceThrowsDownstreamUnavailableException_returnsServerError()
            throws Exception {
        // Given
        var searchInput = new SearchInput("bob");
        Mockito.when(service.search(searchInput)).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/search/%s".formatted(searchInput.getSearchString())))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).search(searchInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeesByNameSearch_whenSearchStringEmpty_returnsStatusBadRequest() throws Exception {
        // Given
        var searchInput = new SearchInput(" ");
        Mockito.when(service.search(searchInput))
                .thenThrow(new ConstraintViolationException("invalid search string", Set.of()));

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/search/%s".formatted(searchInput.getSearchString())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Then
        Mockito.verify(service).search(searchInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenNoEmployeeMatches_returnsStatusNotFound() throws Exception {
        // Given
        var id = UUID.randomUUID().toString();
        Mockito.when(service.findById(id)).thenThrow(new EmployeeNotFoundException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenEmployeeMatches_returnsEmployee() throws Exception {
        // Given
        var uuid = UUID.randomUUID();
        var id = uuid.toString();
        Mockito.when(service.findById(id)).thenReturn(new Employee(uuid, "N", 1, 20, "T", "e@c"));
        var expectedBody =
                """
                {
                    "id": "%s",
                    "name": "N",
                    "salary": 1,
                    "age": 20,
                    "title":"T",
                    "email":"e@c"
                }
                """
                        .formatted(id);

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedBody));

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
        // Given
        var id = UUID.randomUUID().toString();
        Mockito.when(service.findById(id)).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenBadUuid_returnsStatusBadRequest() throws Exception {
        // Given
        var id = "notauuid";
        Mockito.when(service.findById(id)).thenThrow(new ConstraintViolationException("constraint violation", Set.of()));

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getHighestSalaryOfEmployees_whenNoData_returnsStatusNoContent() throws Exception {
        // Given
        Mockito.when(service.findHighestSalaryOfEmployees()).thenReturn(OptionalInt.empty());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verify(service).findHighestSalaryOfEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getHighestSalaryOfEmployees_whenData_returnsInteger() throws Exception {
        // Given
        Mockito.when(service.findHighestSalaryOfEmployees()).thenReturn(OptionalInt.of(100));

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("100"));

        // Then
        Mockito.verify(service).findHighestSalaryOfEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getHighestSalaryOfEmployees_whenServiceThrowsDownstreamUnavailableException_returnsServerError()
            throws Exception {
        // Given
        Mockito.when(service.findHighestSalaryOfEmployees()).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).findHighestSalaryOfEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_whenNoData_returnsStatusNoContent() throws Exception {
        // Given
        Mockito.when(service.findTopTenHighestEarningEmployees()).thenReturn(new ArrayList<>());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verify(service).findTopTenHighestEarningEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_whenData_returnsEmployeeList() throws Exception {
        // Given
        Mockito.when(service.findTopTenHighestEarningEmployees())
                .thenReturn(List.of(
                        "Name 1", "Name 2", "Name 3", "Name 4", "Name 5", "Name 6", "Name 7", "Name 8", "Name 9",
                        "Name 10"));

        var expectedBody =
                """
                [
                    "Name 1",
                    "Name 2",
                    "Name 3",
                    "Name 4",
                    "Name 5",
                    "Name 6",
                    "Name 7",
                    "Name 8",
                    "Name 9",
                    "Name 10"
                ]
                """;

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedBody));

        // Then
        Mockito.verify(service).findTopTenHighestEarningEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_whenServiceThrowsDownstreamUnavailableException_returnsServerError()
            throws Exception {
        // Given
        Mockito.when(service.findTopTenHighestEarningEmployees()).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).findTopTenHighestEarningEmployees();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void createEmployee_whenValidInput_returnsCreatedEmployee() throws Exception {
        // Given
        var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");
        var id = UUID.randomUUID();
        var employeeCreated = new Employee(id, "N", 1, 20, "T", "e@c");
        Mockito.when(service.createEmployee(employeeInput)).thenReturn(employeeCreated);
        var body =
                """
                {
                "name":"N",
                "salary":1,
                "age":20,
                "title":"T"
                }""";
        var expectedBody =
                """
                {
                    "id":"%s",
                    "name":"N",
                    "salary":1,
                    "age":20,
                    "title":"T",
                    "email":"e@c"
                }
                """
                        .formatted(id);

        // When
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedBody));

        // Then
        Mockito.verify(service).createEmployee(employeeInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void createEmployee_whenInvalidInput_returnsStatusBadRequest() throws Exception {
        // Given
        var employeeInput = new CreateEmployeeInput(" ", 1, 20, "T");
        var body =
                """
                {
                "name":" ",
                "salary":1,
                "age":20,
                "title":"T"
                }""";
        Mockito.when(service.createEmployee(employeeInput))
                .thenThrow(new ConstraintViolationException("constraint violation", Set.of()));

        // When
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Then
        Mockito.verify(service).createEmployee(employeeInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void createEmployee_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
        // Given
        var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");
        var body =
                """
                {
                "name":"N",
                "salary":1,
                "age":20,
                "title":"T"
                }""";
        Mockito.when(service.createEmployee(employeeInput)).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).createEmployee(employeeInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void createEmployee_whenCreateEmployeeWithSameNameAsExistingEmployee_returnsConflict() throws Exception {
        // Given
        var employeeInput = new CreateEmployeeInput("N", 1, 20, "T");
        var body =
                """
                {
                "name":"N",
                "salary":1,
                "age":20,
                "title":"T"
                }""";
        Mockito.when(service.createEmployee(employeeInput)).thenThrow(new EmployeeWithNameAlreadyExistsException());

        // When
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        // Then
        Mockito.verify(service).createEmployee(employeeInput);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void deleteEmployeeById_whenNoEmployeeWithIdExists_returnStatusNotFound() throws Exception {
        // Given
        var id = UUID.randomUUID().toString();
        Mockito.when(service.deleteEmployeeById(id)).thenThrow(new EmployeeNotFoundException());

        // When
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Then
        Mockito.verify(service).deleteEmployeeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void deleteEmployeeById_whenEmployeeWithIdExists_returnsEmployeeName() throws Exception {
        // Given
        var id = UUID.randomUUID().toString();
        Mockito.when(service.deleteEmployeeById(id)).thenReturn("N");

        // When
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("N"));

        // Then
        Mockito.verify(service).deleteEmployeeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void deleteEmployeeById_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
        // Given
        var id = UUID.randomUUID().toString();
        Mockito.when(service.deleteEmployeeById(id)).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        // Then
        Mockito.verify(service).deleteEmployeeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void deleteEmployeeById_whenBadUuid_returnsStatusBadRequest() throws Exception {
        // Given
        var id = "notauuid";
        Mockito.when(service.deleteEmployeeById(id)).thenThrow(new ConstraintViolationException("constraint violation", Set.of()));

        // When
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Then
        Mockito.verify(service).deleteEmployeeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }
}
