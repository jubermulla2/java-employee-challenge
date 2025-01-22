package com.reliaquest.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TooManyRequestsExceptionTest {

    @Test
    void testExceptionMessage() {
        // Arrange
        String expectedMessage = "Too many requests, please try again later.";

        // Act
        TooManyRequestsException exception = new TooManyRequestsException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionInheritance() {
        // Arrange & Act
        TooManyRequestsException exception = new TooManyRequestsException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException, "TooManyRequestsException should extend RuntimeException");
    }
}
