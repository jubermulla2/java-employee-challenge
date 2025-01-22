package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void testIsValidUUIDWithValidUUID() {
        String validUUID = "123e4567-e89b-12d3-a456-426614174000";
        boolean result = Util.isValidUUID(validUUID);
        assertTrue(result, "Expected a valid UUID to return true");
    }

    @Test
    void testIsValidUUIDWithInvalidUUID() {
        String invalidUUID = "invalid-uuid-string";
        boolean result = Util.isValidUUID(invalidUUID);
        assertFalse(result, "Expected an invalid UUID to return false");
    }

    @Test
    void testIsValidUUIDWithNull() {
        String nullUUID = null;
        boolean result = Util.isValidUUID(nullUUID);
        assertFalse(result, "Expected null to return false");
    }

    @Test
    void testIsValidUUIDWithEmptyString() {
        String emptyUUID = "";
        boolean result = Util.isValidUUID(emptyUUID);
        assertFalse(result, "Expected an empty string to return false");
    }
}
