package com.reliaquest.api.gateway;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.http.EmployeeMapper;
import com.reliaquest.api.http.Envelope;
import com.reliaquest.api.http.WireEmployee;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeClient implements IEmployeeClient {

    private final RestTemplate restTemplate;

    public EmployeeClient(@Qualifier("employeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAll() {
        var type = new ParameterizedTypeReference<Envelope<List<WireEmployee>>>() {};
        var response = restTemplate.exchange("/employee", HttpMethod.GET, null, type);

        var wire = Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(List.of());

        return wire.stream().map(EmployeeMapper::toDomain).toList();
    }

    public Employee getById(UUID id) throws EmployeeNotFoundException {
        var type = new ParameterizedTypeReference<Envelope<WireEmployee>>() {};
        var response = restTemplate.exchange("/employee/%s".formatted(id), HttpMethod.GET, null, type);

        var wire = Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null);

        return EmployeeMapper.toDomain(wire);
    }

    public Employee create(CreateEmployeeInput in) {
        var type = new ParameterizedTypeReference<Envelope<WireEmployee>>() {};
        var requestWire = EmployeeMapper.toWire(in);
        var entity = new HttpEntity<>(requestWire);
        var response = restTemplate.exchange("/employee", HttpMethod.POST, entity, type);

        var responseWire =
                Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null);

        return EmployeeMapper.toDomain(responseWire);
    }

    public boolean deleteByName(String name) throws EmployeeNotFoundException {
        var type = new ParameterizedTypeReference<Envelope<Boolean>>() {};
        var body = Map.of("name", name);
        var entity = new HttpEntity<>(body);
        var response = restTemplate.exchange("/employee", HttpMethod.DELETE, entity, type);

        return Boolean.TRUE.equals(
                Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null));
    }
}
