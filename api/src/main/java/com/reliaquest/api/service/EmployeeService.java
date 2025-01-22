package com.reliaquest.api.service;

import com.reliaquest.api.Util;
import com.reliaquest.api.constants.ApiConstants;
import com.reliaquest.api.constants.Messages;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.httpclient.IHttpClient;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeCreateRequest;
import com.reliaquest.api.model.EmployeeListResponseData;
import com.reliaquest.api.model.EmployeeResponseData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final IHttpClient httpClient;

    public EmployeeService(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private List<Employee> fetchAllEmployees() {
        try {
            String url = ApiConstants.mockApiBaseUrl;
            ResponseEntity<EmployeeListResponseData> response = httpClient.get(url, EmployeeListResponseData.class);
            if (response.getBody() == null
                    || CollectionUtils.isEmpty(response.getBody().getData())) {
                throw new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT);
            }
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching employees: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while fetching employee list");
        }
        return Collections.emptyList();
    }

    private Employee fetchEmployeeById(String id) {
        if (!Util.isValidUUID(id)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid UUID format: " + id);
        }
        try {
            String url = ApiConstants.mockApiBaseUrl + "/" + id;
            ResponseEntity<EmployeeResponseData> response = httpClient.get(url, EmployeeResponseData.class);
            if (!Objects.nonNull(response)
                    || response.getBody() == null
                    || response.getBody().getData() == null) {
                throw new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT);
            }
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching employee by ID {}: {}", id, e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while fetching employee by ID " + id);
        }
        return null;
    }

    private void handleClientError(HttpClientErrorException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            throw new EmployeeNotFoundException(Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT);
        } else if (statusCode.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            throw new TooManyRequestsException(
                    Messages.YOU_HAVE_EXCEEDED_THE_ALLOWED_NUMBER_OF_REQUESTS_PLEASE_TRY_AGAIN_LATER);
        }
        logger.error("Client error while fetching employees: {}: {}", e.getStatusCode(), e.getMessage());
        throw e;
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Employee search started.");
        List<Employee> employees = fetchAllEmployees();
        logger.info("Total employees count : {}", employees.size());
        return ResponseEntity.ok(employees);
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetEmployeeById")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Employee search started for id: {}", id);
        Employee employee = fetchEmployeeById(id);
        logger.info("Employee search completd with employee details: {}", employee);
        return ResponseEntity.ok(employee);
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackCreateEmployee")
    public ResponseEntity<Employee> createEmployee(EmployeeCreateRequest employeeInput) {
        logger.info("Create employee started for employee input: {}", employeeInput);
        try {
            String url = ApiConstants.mockApiBaseUrl;
            ResponseEntity<EmployeeResponseData> response =
                    httpClient.post(url, employeeInput, EmployeeResponseData.class);
            if (response.getBody() == null || response.getBody().getData() == null) {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR, Messages.FAILED_TO_CREATE_EMPLOYEE);
            }
            return ResponseEntity.ok(response.getBody().getData());
        } catch (HttpClientErrorException e) {
            handleClientError(e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while creating employee: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while creating an employee");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetHighestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Searching for highest Salary started");
        String url = ApiConstants.mockApiBaseUrl;
        List<Employee> employeeList = fetchAllEmployees();
        try {
            Integer highestSalary = employeeList.stream()
                    .map(Employee::getEmployeeSalary)
                    .max(Integer::compare)
                    .orElse(0);
            logger.info("Got highest Salary of employee : {}", highestSalary);
            return ResponseEntity.ok(highestSalary);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching highest salary: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while fetching the highest salary of employees");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetEmployeesByNameSearch")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Employee search for given search straing: {}", searchString);
        if (searchString == null || searchString.trim().isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, Messages.SEARCH_STRING_IS_NULL_OR_EMPTY);
        }
        List<Employee> employees = fetchAllEmployees();
        List<Employee> filteredEmployees = null;
        try {
            filteredEmployees = employees.stream()
                    .filter(employee -> employee.getEmployeeName() != null
                            && employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());

            logger.info("Employee search completed with matching employees count: {}", filteredEmployees.size());
            return ResponseEntity.ok(filteredEmployees);
        } catch (Exception e) {
            logger.error("Unexpected error while searching employees by name {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while searching employees by name");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetTopTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Employee search started for highest earning employee list");
        List<Employee> employees = fetchAllEmployees();
        try {
            List<String> topTenEmployeeNames = employees.stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                    .limit(10)
                    .map(Employee::getEmployeeName)
                    .collect(Collectors.toList());
            logger.info("Employee search completed for top 10 highest salary emp names: {}", topTenEmployeeNames);
            return ResponseEntity.ok(topTenEmployeeNames);
        } catch (Exception e) {
            logger.error("Unexpected error while highest top 10 earning employees name {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while highest top 10 earning employees name");
        }
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDeleteEmployee")
    public ResponseEntity<String> deleteEmployee(String id) {
        logger.info("Employee delete started for employee id: {}", id);
        Employee employee = fetchEmployeeById(id);
        try {
            String employeeName = employee.getEmployeeName();
            DeleteEmployeeRequest deleteRequest = new DeleteEmployeeRequest();
            deleteRequest.setName(employeeName);
            String url = ApiConstants.mockApiBaseUrl;
            ResponseEntity<Void> deleteResponse = httpClient.delete(url, deleteRequest, Void.class);
            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                logger.info("Employee deleted successfully for id: {}", id);
                return ResponseEntity.ok(Messages.EMPLOYEE_HAS_BEEN_SUCCESSFULLY_DELETED_FOR_GIVEN_ID + employeeName);
            } else {
                logger.error(
                        "Failed to delete employee with ID {}: HTTP status {}", id, deleteResponse.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Messages.FAILED_TO_DELETE_EMPLOYEE_WITH_ID + id);
            }
        } catch (HttpClientErrorException e) {
            handleClientError(e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while deleting employee with ID {}: {}", id, e.getMessage(), e);
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while deleting the employee with ID " + id);
        }
    }

    // ------------------------------------------------------------------------------------------------------------------
    // Fallback methods if api service down we have use that response.
    public ResponseEntity<List<Employee>> fallbackGetAllEmployees(Throwable throwable) {
        logger.error("Fallback triggered for getAllEmployees. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of(new Employee()));
    }

    public ResponseEntity<List<Employee>> fallbackGetEmployeesByNameSearch(Throwable throwable) {
        logger.error("Fallback triggered for employee search by name. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of());
    }

    public ResponseEntity<Employee> fallbackGetEmployeeById(Throwable throwable) {
        logger.error("Fallback triggered for getEmployeeById. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<Integer> fallbackGetHighestSalary(Throwable throwable) {
        logger.error("Fallback triggered for getHighestSalary. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(0);
    }

    public ResponseEntity<List<String>> fallbackGetTopTenHighestEarningEmployeeNames(Throwable throwable) {
        logger.error(
                "Fallback triggered for getTopTenHighestEarningEmployeeNames. Reason: {}",
                throwable.getMessage(),
                throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of());
    }

    public ResponseEntity<Employee> fallbackCreateEmployee(Throwable throwable) {
        logger.error("Fallback triggered for employee creation. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<String> fallbackDeleteEmployee(Throwable throwable) {
        logger.error("Fallback triggered for deleteEmployee. Reason: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Failed to delete employee. Please try again later.");
    }
}
