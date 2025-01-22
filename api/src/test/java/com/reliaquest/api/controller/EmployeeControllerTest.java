package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeCreateRequest;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> mockEmployees = Arrays.asList(new Employee(), new Employee());
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployees, response.getBody());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeeById() {
        String employeeId = "123";
        Employee mockEmployee = new Employee();
        when(employeeService.getEmployeeById(employeeId)).thenReturn(ResponseEntity.ok(mockEmployee));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployee, response.getBody());
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void testGetEmployeesByNameSearch() {
        String searchString = "John";
        List<Employee> mockEmployees = Arrays.asList(new Employee(), new Employee());
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployees, response.getBody());
        verify(employeeService, times(1)).getEmployeesByNameSearch(searchString);
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        int highestSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(ResponseEntity.ok(highestSalary));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(highestSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = Arrays.asList("John", "Jane", "Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(ResponseEntity.ok(topTenNames));

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(topTenNames, response.getBody());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void testCreateEmployee() {
        EmployeeCreateRequest mockRequest = new EmployeeCreateRequest();
        Employee mockEmployee = new Employee();
        when(employeeService.createEmployee(mockRequest)).thenReturn(ResponseEntity.ok(mockEmployee));

        ResponseEntity<Employee> response = employeeController.createEmployee(mockRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployee, response.getBody());
        verify(employeeService, times(1)).createEmployee(mockRequest);
    }

    @Test
    void testDeleteEmployeeById() {
        String employeeId = "123";
        String deleteMessage = "Employee deleted successfully.";
        when(employeeService.deleteEmployee(employeeId)).thenReturn(ResponseEntity.ok(deleteMessage));

        ResponseEntity<String> response = employeeController.deleteEmployeeById(employeeId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(deleteMessage, response.getBody());
        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }
}
