package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.response.EmployeeEntityDto;
import com.reliaquest.api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.reliaquest.api.util.UuidUtil.isValidUUID;

@RestController
@RequestMapping("/api/v1/employeeDetails")
public class EmployeeControllerImpl implements IEmployeeController<EmployeeEntityDto, EmployeeCreationDto> {

    private final EmployeeService employeeService;

    /**
     * Constructor for EmployeeControllerImpl.
     *
     * @param employeeService the service to handle employee-related operations.
     */
    @Autowired
    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Endpoint to get all employees.
     *
     * @return ResponseEntity containing a list of EmployeeEntityDto objects.
     */
    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees.")
    @GetMapping()
    public ResponseEntity<List<EmployeeEntityDto>> getAllEmployees() {
        List<EmployeeEntityDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok().body(employees);
    }

    /**
     * Endpoint to search employees by name.
     *
     * @param searchString the string to search for in employee names.
     * @return ResponseEntity containing a list of EmployeeEntityDto objects matching the search criteria.
     */
    @Operation(summary = "Search employees by name", description = "Retrieves a list of employees whose names match the search string.")
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeEntityDto>> getEmployeesByNameSearch(@PathVariable String searchString) {
        if (searchString == null || searchString.isBlank()) {
            throw new IllegalArgumentException("Search string cannot be null or empty");
        }

        List<EmployeeEntityDto> employees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok().body(employees);
    }

    /**
     * Endpoint to get an employee by ID.
     *
     * @param id the ID of the employee to retrieve.
     * @return ResponseEntity containing the EmployeeEntityDto object for the specified ID.
     */
    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their unique ID.")
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

    /**
     * Endpoint to get the highest salary among all employees.
     *
     * @return ResponseEntity containing the highest salary as an Integer.
     */
    @Operation(summary = "Get highest salary of employees", description = "Retrieves the highest salary among all employees.")
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok().body(highestSalary);
    }

    /**
     * Endpoint to get the names of the top ten highest earning employees.
     *
     * @return ResponseEntity containing a list of names of the top ten highest earning employees.
     */
    @Operation(summary = "Get top ten highest earning employee names", description = "Retrieves the names of the top ten highest earning employees.")
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok().body(topTenNames);
    }

    /**
     * Endpoint to create a new employee.
     *
     * @param employeeInput the EmployeeCreationDto object containing the details of the employee to be created.
     * @return ResponseEntity containing the created EmployeeEntityDto object.
     */
    @Operation(summary = "Create a new employee", description = "Creates a new employee with the provided details.")
    @PostMapping()
    public ResponseEntity<EmployeeEntityDto> createEmployee(@RequestBody @Valid EmployeeCreationDto employeeInput) {
        EmployeeEntityDto createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Endpoint to delete an employee by ID.
     *
     * @param id the ID of the employee to delete.s
     * @return ResponseEntity containing the name of the deleted employee.
     */
    @Operation(summary = "Delete employee by ID", description = "Deletes an employee by their unique ID and returns the name of the deleted employee.")
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
