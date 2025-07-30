package com.reliaquest.api.controller;

import com.reliaquest.api.controller.impl.EmployeeControllerImpl;
import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.response.EmployeeEntityDto;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerImplTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeControllerImpl employeeController;

    private EmployeeEntityDto testEmployee1;
    private EmployeeEntityDto testEmployee2;
    private EmployeeCreationDto testEmployeeCreation;
    private UUID validUuid;

    @BeforeEach
    void setUp() {
        validUuid = UUID.randomUUID();

        testEmployee1 = new EmployeeEntityDto();
        testEmployee1.setId(validUuid);
        testEmployee1.setEmployeeName("John Doe");
        testEmployee1.setEmployeeSalary(75000);
        testEmployee1.setEmployeeAge(30);

        testEmployee2 = new EmployeeEntityDto();
        testEmployee2.setId(UUID.randomUUID());
        testEmployee2.setEmployeeName("Jane Smith");
        testEmployee2.setEmployeeSalary(85000);
        testEmployee2.setEmployeeAge(28);

        testEmployeeCreation = new EmployeeCreationDto();
        testEmployeeCreation.setName("New Employee");
        testEmployeeCreation.setSalary(60000);
        testEmployeeCreation.setAge(25);
    }

    // Tests for getAllEmployees()
    @Test
    void getAllEmployees_ShouldReturnListOfEmployees_WhenEmployeesExist() {
        List<EmployeeEntityDto> expectedEmployees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.getAllEmployees()).thenReturn(expectedEmployees);

        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmployees, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenNoEmployeesExist() {
        List<EmployeeEntityDto> emptyList = Collections.emptyList();
        when(employeeService.getAllEmployees()).thenReturn(emptyList);

        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getAllEmployees();
    }

    // Tests for getEmployeesByNameSearch()
    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees_WhenValidSearchString() {
        String searchString = "John";
        List<EmployeeEntityDto> expectedEmployees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(expectedEmployees);

        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmployees, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(employeeService, times(1)).getEmployeesByNameSearch(searchString);
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeesByNameSearch(null)
        );

        assertEquals("Search string cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeesByNameSearch("")
        );

        assertEquals("Search string cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeesByNameSearch("   ")
        );

        assertEquals("Search string cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }

    // Tests for getEmployeeById()
    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenValidIdProvided() {
        when(employeeService.getEmployeeById(validUuid.toString())).thenReturn(testEmployee1);

        ResponseEntity<EmployeeEntityDto> response = employeeController.getEmployeeById(validUuid.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmployee1, response.getBody());
        verify(employeeService, times(1)).getEmployeeById(validUuid.toString());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById(null)
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById("")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById("   ")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsInvalidUUID() {
        String invalidUuid = "invalid-uuid";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById(invalidUuid)
        );

        assertEquals("Invalid UUID format for Employee ID: " + invalidUuid, exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    // Tests for getHighestSalaryOfEmployees()
    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        Integer expectedSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnZero_WhenNoEmployees() {
        Integer expectedSalary = 0;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    // Tests for getTopTenHighestEarningEmployeeNames()
    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnListOfNames() {
        List<String> expectedNames = Arrays.asList("John Doe", "Jane Smith", "Bob Johnson");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNames, response.getBody());
        assertEquals(3, response.getBody().size());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnEmptyList_WhenNoEmployees() {
        List<String> emptyList = Collections.emptyList();
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(emptyList);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    // Tests for createEmployee()
    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenValidInput() {
        when(employeeService.createEmployee(testEmployeeCreation)).thenReturn(testEmployee1);

        ResponseEntity<EmployeeEntityDto> response = employeeController.createEmployee(testEmployeeCreation);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testEmployee1, response.getBody());
        verify(employeeService, times(1)).createEmployee(testEmployeeCreation);
    }

    @Test
    void createEmployee_ShouldCallServiceMethod_WhenCalled() {
        when(employeeService.createEmployee(any(EmployeeCreationDto.class))).thenReturn(testEmployee1);

        employeeController.createEmployee(testEmployeeCreation);

        verify(employeeService, times(1)).createEmployee(testEmployeeCreation);
    }

    // Tests for deleteEmployeeById()
    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenValidIdProvided() {
        String expectedName = "John Doe";
        when(employeeService.deleteEmployeeById(validUuid.toString())).thenReturn(expectedName);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(validUuid.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedName, response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById(validUuid.toString());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById(null)
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById("")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById("   ")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsInvalidUUID() {
        String invalidUuid = "invalid-uuid";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById(invalidUuid)
        );

        assertEquals("Invalid UUID format for Employee ID: " + invalidUuid, exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    // Constructor test
    @Test
    void constructor_ShouldInitializeService() {
        EmployeeService mockService = mock(EmployeeService.class);

        EmployeeControllerImpl controller = new EmployeeControllerImpl(mockService);

        assertNotNull(controller);
    }
}
