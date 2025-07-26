package com.reliaquest.api.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    private UUID id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;

}
