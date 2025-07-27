package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.request.EmployeeDeletionDto;
import com.reliaquest.api.dto.response.*;
import com.reliaquest.api.exception.ResourceNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
    public List<EmployeeEntityDto> getAllEmployees() {
        List<EmployeeEntityDto> employees = new ArrayList<>();

        EmployeeListApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeListApiResponseDto.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            for (EmployeeServerDto employeeDto : response.getData()) {
                EmployeeEntityDto employee = convertToEmployee(employeeDto);
                employees.add(employee);
            }
            log.info("Successfully fetched {} employees", employees.size());
        } else {
            log.warn("No employees found or response is null");
        }

        return employees;
    }

    @Override
    public List<EmployeeEntityDto> getEmployeesByNameSearch(String searchString) {
        List<EmployeeEntityDto> employees = new ArrayList<>();

        EmployeeListApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeListApiResponseDto.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            for (EmployeeServerDto employeeDto : response.getData()) {
                if (employeeDto.getEmployeeName().toLowerCase().contains(searchString.toLowerCase())) {
                    EmployeeEntityDto employee = convertToEmployee(employeeDto);
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
    public EmployeeEntityDto getEmployeeById(String id) {
        EmployeeApiResponseDto response;

        try {
            response = makeHttpRequest(
                    BASE_URL + "/" + id,
                    HttpMethod.GET,
                    null,
                    EmployeeApiResponseDto.class,
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
        EmployeeListApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeListApiResponseDto.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .mapToInt(EmployeeServerDto::getEmployeeSalary)
                    .max()
                    .orElse(0);
        } else {
            log.warn("No employees found or response is null");
            return 0;
        }

    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        EmployeeListApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.GET,
                null,
                EmployeeListApiResponseDto.class,
                null,
                null
        );

        if (response != null && response.getData() != null) {
            PriorityQueue<EmployeeServerDto> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a.getEmployeeSalary(), b.getEmployeeSalary()));
            List<String> topTenNames = new ArrayList<>();

            for (EmployeeServerDto employeeDto : response.getData()) {
                if (employeeDto.getEmployeeSalary() == null) continue;

                minHeap.offer(employeeDto);
                if (minHeap.size() > 10)
                    minHeap.poll();
            }

            while (!minHeap.isEmpty())
                topTenNames.add(minHeap.poll().getEmployeeName());

            log.info("Successfully fetched top ten highest earning employee names: {}", topTenNames);
            return topTenNames;
        } else {
            log.warn("No employees found or response is null");
            return List.of();
        }
    }

    @Override
    public EmployeeEntityDto createEmployee(EmployeeCreationDto employeeInput) {
        EmployeeApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.POST,
                null,
                EmployeeApiResponseDto.class,
                null,
                employeeInput
        );

        if (response != null && response.getData() != null) {
            log.info("Successfully created employee: {}", response.getData());
            return convertToEmployee(response.getData());
        } else {
            throw new RuntimeException("Failed to create employee. Response was null or empty.");
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        EmployeeEntityDto employee = getEmployeeById(id);

        EmployeeDeletionApiResponseDto response = makeHttpRequest(
                BASE_URL,
                HttpMethod.DELETE,
                null,
                EmployeeDeletionApiResponseDto.class,
                null,
                new EmployeeDeletionDto(employee.getEmployeeName())
        );

        if (response != null && response.getData() != null) {
            log.info("Successfully deleted employee: {}", response.getData());
            return employee.getEmployeeName();
        } else {
            log.warn("Failed to delete employee with ID: {}", id);
            return "";
        }
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
    private EmployeeEntityDto convertToEmployee(EmployeeServerDto dto) {
        return EmployeeEntityDto.builder()
                .id(dto.getId())
                .employeeEmail(dto.getEmployeeEmail())
                .employeeName(dto.getEmployeeName())
                .employeeSalary(dto.getEmployeeSalary())
                .employeeTitle(dto.getEmployeeTitle())
                .employeeAge(dto.getEmployeeAge())
                .build();
    }
}
