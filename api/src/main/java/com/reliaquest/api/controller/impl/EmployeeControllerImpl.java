package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.request.EmployeeDto;
import com.reliaquest.api.dto.response.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1/employeeDetails")
public class EmployeeControllerImpl implements IEmployeeController<Employee, EmployeeDto> {

    private final EmployeeService employeeService;

    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid EmployeeDto employeeInput) {
        // Implementation logic here
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        // Implementation logic here
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
