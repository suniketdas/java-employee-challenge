package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.response.Employee;
import com.reliaquest.api.dto.response.EmployeeApiResponse;
import com.reliaquest.api.dto.response.EmployeeByIdApiResponse;
import com.reliaquest.api.dto.response.EmployeeServerDto;
import com.reliaquest.api.exception.ResourceNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    private final RestTemplate restTemplate;

    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        EmployeeApiResponse response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeApiResponse.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            for (EmployeeServerDto employeeDto : response.getData()) {
                Employee employee = convertToEmployee(employeeDto);
                employees.add(employee);
            }
            log.info("Successfully fetched {} employees", employees.size());
        } else {
            log.warn("No employees found or response is null");
        }

        return employees;
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = new ArrayList<>();

        EmployeeApiResponse response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeApiResponse.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            for (EmployeeServerDto employeeDto : response.getData()) {
                if (employeeDto.getEmployee_name().toLowerCase().contains(searchString.toLowerCase())) {
                    Employee employee = convertToEmployee(employeeDto);
                    employees.add(employee);
                }
            }
            log.info("Successfully fetched {} employees", employees.size());
        } else {
            log.warn("No employees found or response is null");
        }

        return employees;
    }

    @Override
    public Employee getEmployeeById(String id) {
        EmployeeByIdApiResponse response;

        try {
            response = makeHttpRequest(
                    BASE_URL + "/" + id,
                    HttpMethod.GET,
                    null,
                    EmployeeByIdApiResponse.class,
                    null,
                    null
            );
        } catch (ResourceNotFoundException ex) {
            throw new IllegalArgumentException("Employee with ID " + id + " not found.");
        }

        if (response != null && response.getData() != null) {
            log.info("Successfully fetched employee: {}", response);

            EmployeeServerDto employeeDto = response.getData();
            return convertToEmployee(employeeDto);
        } else {
            throw new IllegalArgumentException("Employee with ID " + id + " not found.");
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        return 0;
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return List.of();
    }

    @Override
    public Employee createEmployee(EmployeeCreationDto employeeInput) {
        return null;
    }

    @Override
    public String deleteEmployeeById(String id) {
        return "";
    }

    private <T> T makeHttpRequest(
            String url,
            HttpMethod httpMethod,
            HttpHeaders headers,
            Class<T> responseType,
            Map<String, ?> uriVariables,
            Object requestBody
    ) throws HttpClientErrorException {
        HttpEntity<?> entity = (requestBody != null) ? new HttpEntity<>(requestBody, headers)
                : new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    httpMethod,
                    entity,
                    responseType,
                    uriVariables != null ? uriVariables : Map.of()
            );
            return response.getBody();
        } catch (TooManyRequests ex) {
            throw new TooManyRequestsException("Too many requests made to the employee service. Please try again later.");
        } catch (NotFound ex) {
            throw new ResourceNotFoundException("Resource not found at URL: " + url);
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred while making the HTTP request: " + ex.getMessage(), ex);
        }

    }
    private Employee convertToEmployee(EmployeeServerDto dto) {
        return Employee.builder()
                .id(dto.getId())
                .employee_email(dto.getEmployee_email())
                .employee_name(dto.getEmployee_name())
                .employee_salary(dto.getEmployee_salary())
                .employee_title(dto.getEmployee_title())
                .employee_age(dto.getEmployee_age())
                .build();
    }
}
