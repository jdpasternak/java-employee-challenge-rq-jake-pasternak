package com.reliaquest.api.controller;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import com.reliaquest.api.service.EmployeeService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    void getEmployeesByNameSearch_whenNoEmployeesExist_returnsStatusNoContent() throws Exception {
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
    void getEmployeesByNameSearch_whenNoMatchingNameExists_returnsStatusNoContent() {}
    @Test
    void getEmployeesByNameSearch_whenMatchingNameExists_returnsEmployee() {}
    @Test
    void getEmployeesByNameSearch_whenMultipleMatchingNamesExist_returnsListOfEmployees() {}
    @Test
    void getEmployeesByNameSearch_whenConnectRefused_returnsStatusServerError() {}
    @Test
    void getEmployeesByNameSearch_whenSearchStringEmpty_returnsStatusBadRequest() {}

    @Test
    void getEmployeeById() {
    }

    @Test
    void getHighestSalaryOfEmployees() {
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() {
    }

    @Test
    void createEmployee() {
    }

    @Test
    void deleteEmployeeById() {
    }
}