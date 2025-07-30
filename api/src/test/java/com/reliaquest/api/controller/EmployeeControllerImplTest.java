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
        // Assuming EmployeeEntityDto has these fields - adjust based on actual implementation
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
        // Arrange
        List<EmployeeEntityDto> expectedEmployees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.getAllEmployees()).thenReturn(expectedEmployees);

        // Act
        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmployees, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenNoEmployeesExist() {
        // Arrange
        List<EmployeeEntityDto> emptyList = Collections.emptyList();
        when(employeeService.getAllEmployees()).thenReturn(emptyList);

        // Act
        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getAllEmployees();
    }

    // Tests for getEmployeesByNameSearch()
    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees_WhenValidSearchString() {
        // Arrange
        String searchString = "John";
        List<EmployeeEntityDto> expectedEmployees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(expectedEmployees);

        // Act
        ResponseEntity<List<EmployeeEntityDto>> response = employeeController.getEmployeesByNameSearch(searchString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmployees, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(employeeService, times(1)).getEmployeesByNameSearch(searchString);
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeesByNameSearch(null)
        );

        assertEquals("Search string cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeesByNameSearch("")
        );

        assertEquals("Search string cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }

    @Test
    void getEmployeesByNameSearch_ShouldThrowException_WhenSearchStringIsBlank() {
        // Act & Assert
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
        // Arrange
        when(employeeService.getEmployeeById(validUuid.toString())).thenReturn(testEmployee1);

        // Act
        ResponseEntity<EmployeeEntityDto> response = employeeController.getEmployeeById(validUuid.toString());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmployee1, response.getBody());
        verify(employeeService, times(1)).getEmployeeById(validUuid.toString());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById(null)
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById("")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.getEmployeeById("   ")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).getEmployeeById(any());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsInvalidUUID() {
        // Arrange
        String invalidUuid = "invalid-uuid";

        // Act & Assert
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
        // Arrange
        Integer expectedSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);

        // Act
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnZero_WhenNoEmployees() {
        // Arrange
        Integer expectedSalary = 0;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);

        // Act
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    // Tests for getTopTenHighestEarningEmployeeNames()
    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnListOfNames() {
        // Arrange
        List<String> expectedNames = Arrays.asList("John Doe", "Jane Smith", "Bob Johnson");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedNames);

        // Act
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNames, response.getBody());
        assertEquals(3, response.getBody().size());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnEmptyList_WhenNoEmployees() {
        // Arrange
        List<String> emptyList = Collections.emptyList();
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(emptyList);

        // Act
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    // Tests for createEmployee()
    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenValidInput() {
        // Arrange
        when(employeeService.createEmployee(testEmployeeCreation)).thenReturn(testEmployee1);

        // Act
        ResponseEntity<EmployeeEntityDto> response = employeeController.createEmployee(testEmployeeCreation);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testEmployee1, response.getBody());
        verify(employeeService, times(1)).createEmployee(testEmployeeCreation);
    }

    @Test
    void createEmployee_ShouldCallServiceMethod_WhenCalled() {
        // Arrange
        when(employeeService.createEmployee(any(EmployeeCreationDto.class))).thenReturn(testEmployee1);

        // Act
        employeeController.createEmployee(testEmployeeCreation);

        // Assert
        verify(employeeService, times(1)).createEmployee(testEmployeeCreation);
    }

    // Tests for deleteEmployeeById()
    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenValidIdProvided() {
        // Arrange
        String expectedName = "John Doe";
        when(employeeService.deleteEmployeeById(validUuid.toString())).thenReturn(expectedName);

        // Act
        ResponseEntity<String> response = employeeController.deleteEmployeeById(validUuid.toString());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedName, response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById(validUuid.toString());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById(null)
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById("")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeController.deleteEmployeeById("   ")
        );

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenIdIsInvalidUUID() {
        // Arrange
        String invalidUuid = "invalid-uuid";

        // Act & Assert
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
        // Arrange
        EmployeeService mockService = mock(EmployeeService.class);

        // Act
        EmployeeControllerImpl controller = new EmployeeControllerImpl(mockService);

        // Assert
        assertNotNull(controller);
    }
}
