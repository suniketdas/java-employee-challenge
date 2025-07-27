package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.response.EmployeeEntityDto;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.reliaquest.api.util.UuidUtil.isValidUUID;

@RestController
@RequestMapping("/api/v1/employeeDetails")
public class EmployeeControllerImpl implements IEmployeeController<EmployeeEntityDto, EmployeeCreationDto> {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeEntityDto>> getAllEmployees() {
        List<EmployeeEntityDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok().body(employees);
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeEntityDto>> getEmployeesByNameSearch(@PathVariable String searchString) {
        if (searchString == null || searchString.isBlank()) {
            throw new IllegalArgumentException("Search string cannot be null or empty");
        }

        List<EmployeeEntityDto> employees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok().body(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEntityDto> getEmployeeById(@PathVariable String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        if (!isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid UUID format for Employee ID: " + id);
        }

        EmployeeEntityDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok().body(employee);
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok().body(highestSalary);
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok().body(topTenNames);
    }

    @PostMapping()
    public ResponseEntity<EmployeeEntityDto> createEmployee(@RequestBody @Valid EmployeeCreationDto employeeInput) {
        EmployeeEntityDto createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok().body(createdEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        if (!isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid UUID format for Employee ID: " + id);
        }

        String employeeName = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(employeeName);
    }
}
