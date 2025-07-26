package com.reliaquest.api.service;

import com.reliaquest.api.dto.response.Employee;
import java.util.List;
import org.springframework.stereotype.Service;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(Employee employeeInput);

    String deleteEmployeeById(String id);
}
