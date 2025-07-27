package com.reliaquest.api.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeByIdApiResponse {
    private EmployeeServerDto data;
    private String status;
}
