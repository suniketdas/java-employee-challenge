package com.reliaquest.api.service;

import com.reliaquest.api.config.MockEmployeeProperties;
import com.reliaquest.api.dto.request.EmployeeCreationDto;
import com.reliaquest.api.dto.request.EmployeeDeletionDto;
import com.reliaquest.api.dto.response.*;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ResourceNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;

import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MockEmployeeProperties mockEmployeeProperties;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private static final String BASE_URI = "http://localhost:8080/api/v1/employees";

    @BeforeEach
    void setUp() {
        when(mockEmployeeProperties.getUri()).thenReturn(BASE_URI);
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees_WhenEmployeesExist() {
        // Arrange
        List<EmployeeServerDto> serverEmployees = createMockServerEmployees();
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<EmployeeEntityDto> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        EmployeeEntityDto firstEmployee = result.get(0);
        //assertEquals("1", firstEmployee.getId());
        assertEquals("John Doe", firstEmployee.getEmployeeName());
        assertEquals("john.doe@example.com", firstEmployee.getEmployeeEmail());
        assertEquals(50000, firstEmployee.getEmployeeSalary());
        assertEquals("Developer", firstEmployee.getEmployeeTitle());
        assertEquals(30, firstEmployee.getEmployeeAge());
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenNoEmployeesExist() {
        // Arrange
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(null);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<EmployeeEntityDto> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees_WhenSearchStringMatches() {
        // Arrange
        String searchString = "john";
        List<EmployeeServerDto> serverEmployees = createMockServerEmployees();
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<EmployeeEntityDto> result = employeeService.getEmployeesByNameSearch(searchString);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        String searchString = "nonexistent";
        List<EmployeeServerDto> serverEmployees = createMockServerEmployees();
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<EmployeeEntityDto> result = employeeService.getEmployeesByNameSearch(searchString);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        EmployeeServerDto serverEmployee = createMockServerEmployee(employeeId, "John Doe", "john.doe@example.com", 50000, "Developer", 30);
        EmployeeApiResponseDto responseDto = new EmployeeApiResponseDto();
        responseDto.setData(serverEmployee);

        when(restTemplate.exchange(
                eq(BASE_URI + "/" + employeeId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        EmployeeEntityDto result = employeeService.getEmployeeById(employeeId.toString());

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("John Doe", result.getEmployeeName());
    }

    @Test
    void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenEmployeeNotFound() {
        // Arrange
        UUID employeeId = UUID.randomUUID();

        when(restTemplate.exchange(
                eq(BASE_URI + "/" + employeeId.toString()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenThrow(HttpClientErrorException.create(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Not Found",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null
        ));

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId.toString())
        );

        assertEquals("Employee with ID " + employeeId + " not found.", exception.getMessage());
    }

    @Test
    void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenResponseDataIsNull() {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        EmployeeApiResponseDto responseDto = new EmployeeApiResponseDto();
        responseDto.setData(null);

        when(restTemplate.exchange(
                eq(BASE_URI + "/" + employeeId.toString()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId.toString())
        );

        assertEquals("Employee with ID " + employeeId + " not found.", exception.getMessage());
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary_WhenEmployeesExist() {
        // Arrange
        List<EmployeeServerDto> serverEmployees = Arrays.asList(
                createMockServerEmployee(UUID.randomUUID(), "John Doe", "john@example.com", 50000, "Developer", 30),
                createMockServerEmployee(UUID.randomUUID(), "Jane Smith", "jane@example.com", 75000, "Manager", 35),
                createMockServerEmployee(UUID.randomUUID(), "Bob Johnson", "bob@example.com", 60000, "Senior Developer", 32)
        );
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        Integer result = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(75000, result);
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnMinusOne_WhenNoEmployeesExist() {
        // Arrange
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(Arrays.asList());

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        Integer result = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(-1, result);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopTenNames_WhenMoreThanTenEmployees() {
        // Arrange
        List<EmployeeServerDto> serverEmployees = createMockServerEmployeesForTopTen();
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.contains("Employee 15")); // Highest salary
        assertFalse(result.contains("Employee 1")); // Lowest salary
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldSkipEmployeesWithNullSalary() {
        // Arrange
        List<EmployeeServerDto> serverEmployees = Arrays.asList(
                createMockServerEmployee(UUID.randomUUID(), "John Doe", "john@example.com", null, "Developer", 30),
                createMockServerEmployee(UUID.randomUUID(), "Jane Smith", "jane@example.com", 50000, "Manager", 35)
        );
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(serverEmployees);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Smith", result.get(0));
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenSuccessful() {
        // Arrange
        EmployeeCreationDto creationDto = new EmployeeCreationDto();
        creationDto.setName("New Employee");
        creationDto.setSalary(60000);
        creationDto.setAge(28);

        UUID newEmployeeId = UUID.randomUUID();
        EmployeeServerDto createdServerEmployee = createMockServerEmployee(newEmployeeId, "New Employee", "new@example.com", 60000, "Developer", 28);
        EmployeeApiResponseDto responseDto = new EmployeeApiResponseDto();
        responseDto.setData(createdServerEmployee);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(responseDto));

        // Act
        EmployeeEntityDto result = employeeService.createEmployee(creationDto);

        // Assert
        assertNotNull(result);
        assertEquals(newEmployeeId, result.getId());
        assertEquals("New Employee", result.getEmployeeName());
        assertEquals(60000, result.getEmployeeSalary());
    }

    @Test
    void createEmployee_ShouldThrowRuntimeException_WhenResponseIsNull() {
        // Arrange
        EmployeeCreationDto creationDto = new EmployeeCreationDto();
        creationDto.setName("New Employee");

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.createEmployee(creationDto)
        );

        assertEquals("Failed to create employee. Response was null or empty.", exception.getMessage());
    }

    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenSuccessful() {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        String employeeName = "John Doe";

        // Mock getEmployeeById call
        EmployeeServerDto serverEmployee = createMockServerEmployee(employeeId, employeeName, "john@example.com", 50000, "Developer", 30);
        EmployeeApiResponseDto getResponseDto = new EmployeeApiResponseDto();
        getResponseDto.setData(serverEmployee);

        when(restTemplate.exchange(
                eq(BASE_URI + "/" + employeeId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(getResponseDto));

        // Mock delete call
        EmployeeDeletionApiResponseDto deleteResponseDto = new EmployeeDeletionApiResponseDto();
        deleteResponseDto.setData(true);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(EmployeeDeletionApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(deleteResponseDto));

        // Act
        String result = employeeService.deleteEmployeeById(employeeId.toString());

        // Assert
        assertEquals(employeeName, result);

        // Verify the delete request body
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(2)).exchange(
                anyString(),
                any(HttpMethod.class),
                entityCaptor.capture(),
                any(Class.class),
                eq(Map.of())
        );

        // Check the second call (delete call) has the correct body
        HttpEntity<?> deleteEntity = entityCaptor.getAllValues().get(1);
        assertNotNull(deleteEntity.getBody());
        assertTrue(deleteEntity.getBody() instanceof EmployeeDeletionDto);
        EmployeeDeletionDto deletionDto = (EmployeeDeletionDto) deleteEntity.getBody();
        assertEquals(employeeName, deletionDto.getName());
    }

    @Test
    void deleteEmployeeById_ShouldReturnEmptyString_WhenDeleteResponseIsNull() {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        String employeeName = "John Doe";

        // Mock getEmployeeById call
        EmployeeServerDto serverEmployee = createMockServerEmployee(employeeId, employeeName, "john@example.com", 50000, "Developer", 30);
        EmployeeApiResponseDto getResponseDto = new EmployeeApiResponseDto();
        getResponseDto.setData(serverEmployee);

        when(restTemplate.exchange(
                eq(BASE_URI + "/" + employeeId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(getResponseDto));

        // Mock delete call to return null
        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(EmployeeDeletionApiResponseDto.class),
                eq(Map.of())
        )).thenReturn(ResponseEntity.ok(null));

        // Act
        String result = employeeService.deleteEmployeeById(employeeId.toString());

        // Assert
        assertEquals("", result);
    }

    @Test
    void makeHttpRequest_ShouldThrowTooManyRequestsException_WhenTooManyRequestsThrown() {
        // Arrange
        EmployeeListApiResponseDto responseDto = new EmployeeListApiResponseDto();
        responseDto.setData(null);

        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenThrow(HttpClientErrorException.create(
                org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
                "Too Many Requests",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null
        ));

        // Act & Assert
        TooManyRequestsException exception = assertThrows(
                TooManyRequestsException.class,
                () -> employeeService.getAllEmployees()
        );

        assertEquals("Too many requests made to the employee service. Please try again later.", exception.getMessage());
    }

    @Test
    void makeHttpRequest_ShouldThrowRuntimeException_WhenGenericExceptionOccurs() {
        // Arrange
        when(restTemplate.exchange(
                eq(BASE_URI),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EmployeeListApiResponseDto.class),
                eq(Map.of())
        )).thenThrow(new RuntimeException("Generic error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.getAllEmployees()
        );

        assertTrue(exception.getMessage().contains("An error occurred while making the HTTP request"));
    }

    // Helper methods
    private List<EmployeeServerDto> createMockServerEmployees() {
        return Arrays.asList(
                createMockServerEmployee(UUID.randomUUID(), "John Doe", "john.doe@example.com", 50000, "Developer", 30),
                createMockServerEmployee(UUID.randomUUID(), "Jane Smith", "jane.smith@example.com", 60000, "Manager", 35)
        );
    }

    private List<EmployeeServerDto> createMockServerEmployeesForTopTen() {
        List<EmployeeServerDto> employees = Arrays.asList(
                createMockServerEmployee(UUID.randomUUID(), "Employee 1", "emp1@example.com", 30000, "Junior", 25),
                createMockServerEmployee(UUID.randomUUID(), "Employee 2", "emp2@example.com", 35000, "Junior", 26),
                createMockServerEmployee(UUID.randomUUID(), "Employee 3", "emp3@example.com", 40000, "Junior", 27),
                createMockServerEmployee(UUID.randomUUID(), "Employee 4", "emp4@example.com", 45000, "Mid", 28),
                createMockServerEmployee(UUID.randomUUID(), "Employee 5", "emp5@example.com", 50000, "Mid", 29),
                createMockServerEmployee(UUID.randomUUID(), "Employee 6", "emp6@example.com", 55000, "Mid", 30),
                createMockServerEmployee(UUID.randomUUID(), "Employee 7", "emp7@example.com", 60000, "Senior", 31),
                createMockServerEmployee(UUID.randomUUID(), "Employee 8", "emp8@example.com", 65000, "Senior", 32),
                createMockServerEmployee(UUID.randomUUID(), "Employee 9", "emp9@example.com", 70000, "Senior", 33),
                createMockServerEmployee(UUID.randomUUID(), "Employee 10", "emp10@example.com", 75000, "Lead", 34),
                createMockServerEmployee(UUID.randomUUID(), "Employee 11", "emp11@example.com", 80000, "Lead", 35),
                createMockServerEmployee(UUID.randomUUID(), "Employee 12", "emp12@example.com", 85000, "Manager", 36),
                createMockServerEmployee(UUID.randomUUID(), "Employee 13", "emp13@example.com", 90000, "Manager", 37),
                createMockServerEmployee(UUID.randomUUID(), "Employee 14", "emp14@example.com", 95000, "Director", 38),
                createMockServerEmployee(UUID.randomUUID(), "Employee 15", "emp15@example.com", 100000, "Director", 39)
        );
        return employees;
    }

    private EmployeeServerDto createMockServerEmployee(UUID id, String name, String email, Integer salary, String title, Integer age) {
        EmployeeServerDto employee = new EmployeeServerDto();
        employee.setId(id);
        employee.setEmployeeName(name);
        employee.setEmployeeEmail(email);
        employee.setEmployeeSalary(salary);
        employee.setEmployeeTitle(title);
        employee.setEmployeeAge(age);
        return employee;
    }
}