package com.reliaquest.api.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.reliaquest.api.constants.Messages;
import org.junit.jupiter.api.Test;

class MessagesTest {

    @Test
    void testConstants() {
        assertEquals("Search string is null or empty.", Messages.SEARCH_STRING_IS_NULL_OR_EMPTY);
        assertEquals("Failed to create employee.", Messages.FAILED_TO_CREATE_EMPLOYEE);
        assertEquals("Failed to delete employee for given id.", Messages.FAILED_TO_DELETE_EMPLOYEE_WITH_ID);
        assertEquals(
                "Employee has been successfully deleted for given id.",
                Messages.EMPLOYEE_HAS_BEEN_SUCCESSFULLY_DELETED_FOR_GIVEN_ID);
        assertEquals(
                "You have exceeded the allowed number of requests. Please try again later.",
                Messages.YOU_HAVE_EXCEEDED_THE_ALLOWED_NUMBER_OF_REQUESTS_PLEASE_TRY_AGAIN_LATER);
        assertEquals("No employee data found given input ", Messages.NO_EMPLOYEE_DATA_FOUND_FOR_GIVEN_INPUT);
        assertEquals("Server error", Messages.SERVER_ERROR);
        assertEquals("Not Found", Messages.NOT_FOUND);
        assertEquals("Too Many Requests", Messages.TOO_MANY_REQUESTS);
        assertEquals("Client error", Messages.CLIENT_ERROR);
    }
}
