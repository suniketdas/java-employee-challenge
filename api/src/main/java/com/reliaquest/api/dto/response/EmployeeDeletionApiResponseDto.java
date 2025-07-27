package com.reliaquest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeDeletionApiResponseDto {
    @JsonProperty("data")
    private Boolean data;

    @JsonProperty("status")
    private String status;
}
