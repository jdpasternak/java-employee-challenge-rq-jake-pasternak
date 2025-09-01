package com.reliaquest.api.helper;

import com.reliaquest.api.model.Employee;
import java.util.Comparator;
import java.util.Locale;

public final class EmployeeComparators {
    private EmployeeComparators() {}

    public static final Comparator<Employee> BY_SALARY_DESC_NAME_ASC_ID_ASC = Comparator.comparingInt((Employee e) -> {
                Integer s = e == null ? null : e.getSalary();
                return s == null ? Integer.MIN_VALUE : s;
            })
            .reversed()
            .thenComparing(e -> {
                String n = e == null ? null : e.getName();
                return n == null ? "" : n.toLowerCase(Locale.ROOT);
            })
            .thenComparing(Employee::getId, Comparator.nullsLast(Comparator.naturalOrder()));
}
