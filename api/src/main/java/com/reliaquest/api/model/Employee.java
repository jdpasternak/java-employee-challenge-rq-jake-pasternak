package com.reliaquest.api.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    private String id;
    private String name;
    private int salary;
    private int age;
    private String title;
    private String email;
}
