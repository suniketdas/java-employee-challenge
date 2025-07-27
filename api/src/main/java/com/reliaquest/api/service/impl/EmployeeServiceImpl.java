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

    /**
     * Constructor for EmployeeServiceImpl.
     *
     * @param restTemplate the RestTemplate to make HTTP requests.
     */
    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches all employees from the external API and converts them to EmployeeEntityDto objects.
     *
     * @return List of EmployeeEntityDto objects representing all employees.
     */
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

    /**
     * Searches for employees by name in the external API and returns a list of matching EmployeeEntityDto objects.
     *
     * @param searchString the string to search for in employee names.
     * @return List of EmployeeEntityDto objects matching the search criteria.
     */
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

    /**
     * Fetches an employee by ID from the external API and converts it to an EmployeeEntityDto object.
     *
     * @param id the ID of the employee to retrieve.
     * @return EmployeeEntityDto object representing the employee with the specified ID.
     * @throws IllegalArgumentException if the employee with the specified ID is not found.
     */
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

    /**
     * Fetches the highest salary among all employees from the external API.
     *
     * @return Integer representing the highest salary of employees.
     */
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

    /**
     * Fetches the names of the top ten highest earning employees from the external API.
     *
     * @return List of names of the top ten highest earning employees.
     */
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

    /**
     * Creates a new employee using the external API and returns the created EmployeeEntityDto object.
     *
     * @param employeeInput the EmployeeCreationDto object containing the details of the employee to create.
     * @return EmployeeEntityDto object representing the created employee.
     */
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

    /**
     * Deletes an employee by ID using the external API and returns the name of the deleted employee.
     *
     * @param id the ID of the employee to delete.
     * @return String representing the name of the deleted employee.
     */
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

    /**
     * Makes an HTTP request to the specified URL with the given parameters.
     *
     * @param url          the URL to make the request to.
     * @param httpMethod   the HTTP method to use (GET, POST, DELETE, etc.).
     * @param headers      the HTTP headers to include in the request.
     * @param responseType the type of response expected.
     * @param uriVariables variables to be replaced in the URL.
     * @param requestBody  the body of the request (if applicable).
     * @return the response body of type T.
     * @throws HttpClientErrorException if an error occurs during the HTTP request.
     */
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

    /**
     * Converts an EmployeeServerDto object to an EmployeeEntityDto object.
     *
     * @param dto the EmployeeServerDto object to convert.
     * @return EmployeeEntityDto object representing the employee.
     */
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
