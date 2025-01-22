package com.reliaquest.api.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.reliaquest.api.constants.ApiConstants;
import org.junit.jupiter.api.Test;

public class ApiConstantsTest {

    @Test
    void testMockApiBaseUrl() {
        String expectedUrl = "http://localhost:8112/api/v1/employee";
        assertEquals(expectedUrl, ApiConstants.mockApiBaseUrl, "The mockApiBaseUrl should match the expected URL.");
    }
}
