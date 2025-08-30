package com.reliaquest.api.http;

import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
                () -> Assertions.assertEquals("e@c", result.getEmail())
        );
    }

    @Test
    void toDomain_whenSalaryNull_throwsNullPointerException() {
        // Given
        String id = UUID.randomUUID().toString();
        var wire = new WireEmployee(id, "N", null, 20, "T", "e@c");

        // When
        Assertions.assertThrows(NullPointerException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }

    @Test
    void toDomain_whenAgeNull_throwsNullPointerException() {
        // Given
        String id = UUID.randomUUID().toString();
        var wire = new WireEmployee(id, "N", 1, null, "T", "e@c");

        // When
        Assertions.assertThrows(NullPointerException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }

    @Test
    void toDomain_whenIdNotUuid_throwsIllegalArgumentException() {
        // Given
        String id = "notauuid";
        var wire = new WireEmployee(id, "N", 1, 20, "T", "e@c");

        // When
        Assertions.assertThrows(IllegalArgumentException.class, () -> EmployeeMapper.toDomain(wire));

        // Then

    }
}