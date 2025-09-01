package com.reliaquest.api.gateway;

import static com.reliaquest.api.gateway.GatewayConstants.EMPLOYEE_ENDPOINT;
import static com.reliaquest.api.gateway.GatewayConstants.NAME;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.http.Envelope;
import com.reliaquest.api.http.WireEmployee;
import com.reliaquest.api.http.mapper.EmployeeMapper;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeClient implements IEmployeeClient {

    private final RestTemplate restTemplate;

    public EmployeeClient(@Qualifier("employeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAll() {
        return ioSafe(() -> {
            var type = new ParameterizedTypeReference<Envelope<List<WireEmployee>>>() {};
            var response = restTemplate.exchange(EMPLOYEE_ENDPOINT, HttpMethod.GET, null, type);

            var wire =
                    Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(List.of());

            return wire.stream().map(EmployeeMapper::toDomain).toList();
        });
    }

    public Employee getById(UUID id) {
        return ioSafe(() -> {
            var type = new ParameterizedTypeReference<Envelope<WireEmployee>>() {};
            var response = restTemplate.exchange("%s/%s".formatted(EMPLOYEE_ENDPOINT, id), HttpMethod.GET, null, type);

            var wire =
                    Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null);

            return EmployeeMapper.toDomain(wire);
        });
    }

    public Employee create(CreateEmployeeInput in) {
        return ioSafe(() -> {
            var type = new ParameterizedTypeReference<Envelope<WireEmployee>>() {};
            var requestWire = EmployeeMapper.toWire(in);
            var entity = new HttpEntity<>(requestWire);
            var response = restTemplate.exchange(EMPLOYEE_ENDPOINT, HttpMethod.POST, entity, type);

            var responseWire =
                    Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null);

            return EmployeeMapper.toDomain(responseWire);
        });
    }

    public boolean deleteByName(String name) {
        return ioSafe(() -> {
            var type = new ParameterizedTypeReference<Envelope<Boolean>>() {};
            var body = Map.of(NAME, name);
            var entity = new HttpEntity<>(body);
            var response = restTemplate.exchange(EMPLOYEE_ENDPOINT, HttpMethod.DELETE, entity, type);

            return Boolean.TRUE.equals(
                    Optional.ofNullable(response.getBody()).map(Envelope::data).orElse(null));
        });
    }

    private <T> T ioSafe(Supplier<T> call) {
        try {
            return call.get();
        } catch (ResourceAccessException resourceAccessException) {
            throw new DownstreamUnavailableException("downstream unreachable");
        } catch (RestClientException restClientException) {
            throw new DownstreamUnavailableException("downstream client error");
        }
    }
}
