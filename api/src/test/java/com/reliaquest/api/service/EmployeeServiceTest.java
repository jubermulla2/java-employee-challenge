package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.reliaquest.api.constants.Messages;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.httpclient.IHttpClient;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeCreateRequest;
import com.reliaquest.api.model.EmployeeListResponseData;
import com.reliaquest.api.model.EmployeeResponseData;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

class EmployeeServiceTest {

    @Mock
    private IHttpClient httpClient;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees_Success() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee(
                        "25d32a9d-67a1-4552-8d3a-cf291c489887",
                        "John Doe",
                        50000,
                        30,
                        "Engineer",
                        "john.doe@example.com"),
                new Employee("2", "Jane Smith", 60000, 25, "Manager", "jane.smith@example.com"));

        EmployeeListResponseData responseData = new EmployeeListResponseData();
        responseData.setData(mockEmployees);
        when(httpClient.get(anyString(), eq(EmployeeListResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getEmployeeName());
    }

    @Test
    void testGetAllEmployees_NoDataFound() {
        EmployeeListResponseData responseData = new EmployeeListResponseData();
        responseData.setData(Collections.emptyList());
        when(httpClient.get(anyString(), eq(EmployeeListResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals(Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT, exception.getMessage());
    }

    @Test
    void testGetEmployeeById_Success() {
        Employee mockEmployee = new Employee(
                "25d32a9d-67a1-4552-8d3a-cf291c489887", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponseData responseData = new EmployeeResponseData();
        responseData.setData(mockEmployee);

        when(httpClient.get(contains("/25d32a9d-67a1-4552-8d3a-cf291c489887"), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<Employee> response = employeeService.getEmployeeById("25d32a9d-67a1-4552-8d3a-cf291c489887");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getEmployeeName());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(httpClient.get(contains("/1"), eq(EmployeeResponseData.class)))
                .thenThrow(new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("25d32a9d-67a1-4552-8d3a-cf291c489887");
        });

        assertEquals(Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT, exception.getMessage());
    }

    @Test
    void testCreateEmployee_Success() {
        EmployeeCreateRequest createRequest = new EmployeeCreateRequest("John Doe", 50000, 30, "Engineer");
        Employee mockEmployee = new Employee(
                "25d32a9d-67a1-4552-8d3a-cf291c489887", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponseData responseData = new EmployeeResponseData();
        responseData.setData(mockEmployee);

        when(httpClient.post(anyString(), eq(createRequest), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(responseData, HttpStatus.OK));

        ResponseEntity<Employee> response = employeeService.createEmployee(createRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getEmployeeName());
    }

    @Test
    void testDeleteEmployee_Success() {
        Employee mockEmployee = new Employee(
                "25d32a9d-67a1-4552-8d3a-cf291c489887", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");

        EmployeeResponseData employeeResponseData1 = new EmployeeResponseData();
        employeeResponseData1.setData(mockEmployee);
        when(httpClient.get(contains("/25d32a9d-67a1-4552-8d3a-cf291c489887"), eq(EmployeeResponseData.class)))
                .thenReturn(new ResponseEntity<>(employeeResponseData1, HttpStatus.OK));

        when(httpClient.delete(anyString(), any(DeleteEmployeeRequest.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        ResponseEntity<String> response = employeeService.deleteEmployee("25d32a9d-67a1-4552-8d3a-cf291c489887");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Messages.EMPLOYEE_HAS_BEEN_SUCCESSFULLY_DELETED_FOR_GIVEN_ID + "John Doe", response.getBody());
    }

    @Test
    void testFallbackGetAllEmployees() {
        Throwable throwable = new RuntimeException("Service down");
        ResponseEntity<List<Employee>> response = employeeService.fallbackGetAllEmployees(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFallbackGetEmployeesByNameSearch() {
        Throwable throwable = new RuntimeException("Service unavailable");
        ResponseEntity<List<Employee>> response = employeeService.fallbackGetEmployeesByNameSearch(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFallbackGetEmployeeById() {
        Throwable throwable = new RuntimeException("Service unavailable");
        ResponseEntity<Employee> response = employeeService.fallbackGetEmployeeById(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFallbackGetHighestSalary() {
        Throwable throwable = new RuntimeException("Service error");
        ResponseEntity<Integer> response = employeeService.fallbackGetHighestSalary(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody());
    }

    @Test
    void testFallbackGetTopTenHighestEarningEmployeeNames() {
        Throwable throwable = new RuntimeException("Service issue");
        ResponseEntity<List<String>> response = employeeService.fallbackGetTopTenHighestEarningEmployeeNames(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFallbackCreateEmployee() {
        Throwable throwable = new RuntimeException("Service not reachable");
        ResponseEntity<Employee> response = employeeService.fallbackCreateEmployee(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFallbackDeleteEmployee() {
        Throwable throwable = new RuntimeException("Delete operation failed");
        ResponseEntity<String> response = employeeService.fallbackDeleteEmployee(throwable);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to delete employee. Please try again later.", response.getBody());
    }
}
