package com.reliaquest.api.gateway;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.http.EmployeeMapper;
import com.reliaquest.api.http.Envelope;
import com.reliaquest.api.http.WireEmployee;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmployeeClient implements IEmployeeClient {

    private final RestTemplate restTemplate;

    public EmployeeClient(@Qualifier("employeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAll() {
        var type = new ParameterizedTypeReference<Envelope<List<WireEmployee>>>() {
        };
        var response = restTemplate.exchange("/employee", HttpMethod.GET, null, type);

        var wire = Optional.ofNullable(response.getBody())
                .map(Envelope::data)
                .orElse(List.of());

        return wire.stream().map(EmployeeMapper::toDomain).toList();
    }

    public Employee getById(UUID id) throws EmployeeNotFoundException {
        return null;
    }

    public Employee create(CreateEmployeeInput in) {
        return null;
    }

    public boolean deleteByName(String name) throws EmployeeNotFoundException {
        return false;
    }
}
