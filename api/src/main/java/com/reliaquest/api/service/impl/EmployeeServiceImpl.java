package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.response.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
        // This method would typically make a REST call to the base URL to fetch all employees.
        List<Employee> employees = new ArrayList<>();
        JSONObject resp = makeGetRequest(BASE_URL);
        log.info("Response from Employee API: {}", resp.getJSONArray("data"));
        employees.addAll(resp.getJSONArray("data").toList().stream()
                        .map(obj -> (Map<?, ?>) obj)
                        .map(obj -> new Employee(
                                obj.get("id") == null ? null : obj.get("id").toString(),
                                obj.get("name") == null ? null : obj.get("name").toString(),
                                obj.get("salary") == null ? null : (Integer) obj.get("salary"),
                                obj.get("age") == null ? null : (Integer) obj.get("age"),
                                obj.get("title") == null ? null : obj.get("title").toString(),
                                obj.get("email") == null ? null : obj.get("email").toString()))
                        .toList());
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return List.of();
    }

    @Override
    public Employee getEmployeeById(String id) {
        return null;
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
    public Employee createEmployee(Employee employeeInput) {
        return null;
    }

    @Override
    public String deleteEmployeeById(String id) {
        return "";
    }

    private JSONObject makeGetRequest(String url) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return new JSONObject(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error : " + e.getMessage(), e);
        }
    }
}
