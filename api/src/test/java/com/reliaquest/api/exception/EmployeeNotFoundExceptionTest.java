package com.reliaquest.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EmployeeNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        // Arrange
        String expectedMessage = "Employee not found";

        // Act
        EmployeeNotFoundException exception = new EmployeeNotFoundException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionInheritance() {
        // Arrange & Act
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException, "EmployeeNotFoundException should extend RuntimeException");
    }
}
