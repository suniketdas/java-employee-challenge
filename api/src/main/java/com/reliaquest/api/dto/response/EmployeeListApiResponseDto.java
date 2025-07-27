package com.reliaquest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeListApiResponseDto {
    @JsonProperty("data")
    private List<EmployeeServerDto> data;

    @JsonProperty("status")
    private String status;
}
