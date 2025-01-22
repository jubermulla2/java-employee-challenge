package com.reliaquest.api.model;

import lombok.Data;

@Data
public class EmployeeResponseData {
    private Employee data;
    private String message;
    private String status;
}
