package com.reliaquest.api.service;

import static com.reliaquest.api.cache.CacheNames.EMPLOYEES_ALL;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeWithNameAlreadyExistsException;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.helper.EmployeeComparators;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.SearchInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeClient client;
    private final EmployeeReadService readService;

    @Cacheable(cacheNames = EMPLOYEES_ALL)
    public List<Employee> findAll() {
        return readService.findAll();
    }

    public List<Employee> search(@Valid SearchInput searchInput) {
        String normalizedSearchString = Normalizer.normalize(searchInput.getSearchString(), Normalizer.Form.NFKD)
                .toLowerCase(Locale.ROOT);
        return readService.findAll().stream()
                .filter(employee -> Normalizer.normalize(employee.getName(), Normalizer.Form.NFKD)
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedSearchString))
                .toList();
    }

    public Employee findById(@NotNull @org.hibernate.validator.constraints.UUID String id) {
        return readService.findAll().stream()
                .filter(e -> e.getId().equals(UUID.fromString(id)))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public OptionalInt findHighestSalaryOfEmployees() {
        List<Employee> employees = readService.findAll();
        return employees.stream().mapToInt(Employee::getSalary).max();
    }

    public List<String> findTopTenHighestEarningEmployees() {
        List<Employee> employees = readService.findAll();
        return employees.stream()
                .sorted(EmployeeComparators.BY_SALARY_DESC_NAME_ASC_ID_ASC)
                .limit(10)
                .map(Employee::getName)
                .toList();
    }

    @CacheEvict(cacheNames = EMPLOYEES_ALL, allEntries = true)
    public Employee createEmployee(@Valid CreateEmployeeInput employeeInput) {
        String employeeName = employeeInput.name();
        String employeeNameNormalized = employeeName.toLowerCase(Locale.ROOT);
        List<Employee> employees = readService.findAll();
        if (employees.stream()
                .map(Employee::getName)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .anyMatch(employeeNameNormalized::equals)) {
            throw new EmployeeWithNameAlreadyExistsException(employeeName);
        }
        return client.create(employeeInput);
    }

    @CacheEvict(cacheNames = EMPLOYEES_ALL, allEntries = true)
    public String deleteEmployeeById(@NotNull @org.hibernate.validator.constraints.UUID String id)
            throws EmployeeNotFoundException {
        var employeeFound = readService.findAll().stream()
                .filter(e -> e.getId().equals(UUID.fromString(id)))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        String name = employeeFound.getName();
        boolean deleted = client.deleteByName(name);
        if (!deleted) {
            throw new EmployeeNotFoundException(id);
        }
        return name;
    }
}
