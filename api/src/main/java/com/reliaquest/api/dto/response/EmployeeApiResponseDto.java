package com.reliaquest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeApiResponseDto {
    @JsonProperty("data")
    private EmployeeServerDto data;

    @JsonProperty("status")
    private String status;
}
