package com.reliaquest.api.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private UUID id;
    private String name;
    private int salary;
    private int age;
    private String title;
    private String email;
}
