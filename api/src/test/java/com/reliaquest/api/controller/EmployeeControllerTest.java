package com.reliaquest.api.controller;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.ConstraintViolationException;
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

import java.util.*;

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
                new Employee(UUID.randomUUID(), "N5", 1, 20, "T5", "e5@c")
        );
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
        Mockito.when(service.search(searchInput)).thenReturn(List.of(
                new Employee(idB, "Bob Bobster",1, 20, "T", "eb@c"),
                new Employee(idJ, "Jane Bobster",1, 20, "T", "ej@c")
        ));
        var expectedBody = """
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
                """.formatted(idB, idJ);

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
    void getEmployeesByNameSearch_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
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
        Mockito.when(service.search(searchInput)).thenThrow(new ConstraintViolationException("invalid search string", Set.of()));

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
        var id = UUID.randomUUID();
        Mockito.when(service.findById(id)).thenThrow(new EmployeeNotFoundException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenEmployeeMatches_returnsEmployee() throws Exception {
        // Given
        var id = UUID.randomUUID();
        Mockito.when(service.findById(id)).thenReturn(new Employee(id, "N", 1, 20, "T", "e@c"));
        var expectedBody = """
                {
                    "id": "%s",
                    "name": "N",
                    "salary": 1,
                    "age": 20,
                    "title":"T",
                    "email":"e@c"
                }
                """.formatted(id);

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
        var id = UUID.randomUUID();
        Mockito.when(service.findById(id)).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verify(service).findById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeeById_whenBadUuid_returnsStatusBadRequest() throws Exception {
        // Given
        var id = "notauuid";

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/%s".formatted(id)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(""));

        // Then
        Mockito.verifyNoInteractions(service);
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
    void getHighestSalaryOfEmployees_whenServiceThrowsDownstreamUnavailableException_returnsServerError() throws Exception {
        // Given
        Mockito.when(service.findHighestSalaryOfEmployees()).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));

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
    void getTopTenHighestEarningEmployeeNames_whenData_returnsEmployeeList() {
    }
    @Test
    void getTopTenHighestEarningEmployeeNames_whenServiceThrowsDownstreamUnavailableException_returnsServerError() {
    }

    @Test
    void createEmployee() {
    }

    @Test
    void deleteEmployeeById() {
    }
}