package com.reliaquest.api.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeServerDto {
    private UUID id;
    private String employee_email;
    private String employee_name;
    private Integer employee_salary;
    private String employee_title;
    private Integer employee_age;
}
