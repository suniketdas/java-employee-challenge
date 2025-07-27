package com.reliaquest.api.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeEntityDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("employee_email")
    private String employeeEmail;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private Integer employeeSalary;

    @JsonProperty("employee_title")
    private String employeeTitle;

    @JsonProperty("employee_age")
    private Integer employeeAge;
}
