package com.reliaquest.api.http;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.controller.EmployeeControllerAdvice;
import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CorrelationFilterTest {
    @Mock
    EmployeeService service;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new EmployeeController(service))
                .setControllerAdvice(new EmployeeControllerAdvice())
                .addFilters(new CorrelationFilter())
                .build();
    }

    @Test
    void doFilterInternal_whenHeaderMissing_echoesHeader() throws Exception {
        // Given
        Mockito.when(service.findAll()).thenReturn(List.of(new Employee(UUID.randomUUID(), "N", 1, 20, "T", "e@c")));

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("X-Correlation-Id"))
                .andExpect(result -> {
                    var correlationId = result.getResponse().getHeader("X-Correlation-Id");
                    Assertions.assertNotNull(correlationId);
                    Assertions.assertFalse(correlationId.isBlank());
                });

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void doFilterInternal_whenHeaderExists_echoesHeaderUnchanged() throws Exception {
        // Given
        Mockito.when(service.findAll()).thenReturn(List.of(new Employee(UUID.randomUUID(), "N", 1, 20, "T", "e@c")));

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee").header("X-Correlation-Id", "abc123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("X-Correlation-Id", "abc123"));

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void doFilterInternal_whenErrorStatus_echoesHeader() throws Exception {
        // Given
        Mockito.when(service.findAll()).thenThrow(new DownstreamUnavailableException());

        // When
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.header().exists("X-Correlation-Id"));

        // Then
        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }
}
