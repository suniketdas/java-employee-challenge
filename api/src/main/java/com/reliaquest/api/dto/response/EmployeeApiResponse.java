package com.reliaquest.api.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeApiResponse {
    private List<EmployeeServerDto> data;
    private String status;
}
