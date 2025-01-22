package com.reliaquest.api.model;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeListResponseData {
    private List<Employee> data;
    private String message;
}
