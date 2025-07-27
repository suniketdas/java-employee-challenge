package com.reliaquest.api.service;

import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.response.EmployeeEntityDto;
import java.util.List;

public interface EmployeeService {
    List<EmployeeEntityDto> getAllEmployees();

    List<EmployeeEntityDto> getEmployeesByNameSearch(String searchString);

    EmployeeEntityDto getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    EmployeeEntityDto createEmployee(EmployeeCreationDto employeeInput);

    String deleteEmployeeById(String id);
}
