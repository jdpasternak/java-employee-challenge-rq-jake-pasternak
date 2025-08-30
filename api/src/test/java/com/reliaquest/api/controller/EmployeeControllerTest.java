package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
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

        var expectedBody = """
                {
                    "status":"Successfully processed request."
                }
                """;

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Then
        Mockito.verify(service, Mockito.never()).findAll();
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void getEmployeesByNameSearch() {
    }

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