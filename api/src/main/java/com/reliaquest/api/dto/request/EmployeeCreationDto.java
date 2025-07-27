package com.reliaquest.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeCreationDto {
    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @JsonProperty("salary")
    @NotNull(message = "Salary is required")
    @Min(value = 1, message = "Salary must be greater than 0")
    private Integer salary;

    @JsonProperty("age")
    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must be at most 75")
    private Integer age;

    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank")
    private String title;
}
