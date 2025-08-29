package com.reliaquest.api.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    private UUID id;
    private String name;
    private int salary;
    private int age;
    private String title;
    private String email;
}
