package com.reliaquest.api.http;

import com.reliaquest.api.exception.BadGatewayException;
import com.reliaquest.api.model.Employee;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

    @Test
    void toDomain_whenValidInput_returnsEmployee() {
        // Given
        String id = UUID.randomUUID().toString();
        var wire = new WireEmployee(id, "N", 1, 20, "T", "e@c");

        // When
        Employee result = EmployeeMapper.toDomain(wire);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertAll(
                () -> Assertions.assertEquals(UUID.fromString(id), result.getId()),
                () -> Assertions.assertEquals("N", result.getName()),
                () -> Assertions.assertEquals(1, result.getSalary()),
                () -> Assertions.assertEquals(20, result.getAge()),
                () -> Assertions.assertEquals("T", result.getTitle()),
                () -> Assertions.assertEquals("e@c", result.getEmail()));
    }

    @Test
    void toDomain_whenSalaryNull_throwsBadGatewayException() {
        // Given
        String id = UUID.randomUUID().toString();
        var wire = new WireEmployee(id, "N", null, 20, "T", "e@c");

        // When
        Assertions.assertThrows(BadGatewayException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }

    @Test
    void toDomain_whenAgeNull_throwsBadGatewayException() {
        // Given
        String id = UUID.randomUUID().toString();
        var wire = new WireEmployee(id, "N", 1, null, "T", "e@c");

        // When
        Assertions.assertThrows(BadGatewayException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }

    @Test
    void toDomain_whenIdNotUuid_throwsBadGatewayException() {
        // Given
        String id = "notauuid";
        var wire = new WireEmployee(id, "N", 1, 20, "T", "e@c");

        // When
        Assertions.assertThrows(BadGatewayException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }
}
