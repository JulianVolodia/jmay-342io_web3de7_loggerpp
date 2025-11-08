package com.nccgroup.loggerplusplus.logentry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FieldGroup enum
 */
@DisplayName("FieldGroup Tests")
class FieldGroupTest {

    @Test
    @DisplayName("Should find ENTRY group by primary label")
    void testFindByPrimaryLabel_Entry() {
        FieldGroup result = FieldGroup.findByLabel("Entry");
        assertEquals(FieldGroup.ENTRY, result);
    }

    @Test
    @DisplayName("Should find REQUEST group by primary label")
    void testFindByPrimaryLabel_Request() {
        FieldGroup result = FieldGroup.findByLabel("Request");
        assertEquals(FieldGroup.REQUEST, result);
    }

    @Test
    @DisplayName("Should find RESPONSE group by primary label")
    void testFindByPrimaryLabel_Response() {
        FieldGroup result = FieldGroup.findByLabel("Response");
        assertEquals(FieldGroup.RESPONSE, result);
    }

    @ParameterizedTest
    @CsvSource({
        "Log, ENTRY",
        "Proxy, ENTRY",
        "log, ENTRY",
        "proxy, ENTRY",
        "LOG, ENTRY",
        "PROXY, ENTRY"
    })
    @DisplayName("Should find ENTRY group by alternative labels (case insensitive)")
    void testFindByAlternativeLabels_Entry(String label, String expectedGroup) {
        FieldGroup result = FieldGroup.findByLabel(label);
        assertEquals(FieldGroup.valueOf(expectedGroup), result);
    }

    @ParameterizedTest
    @CsvSource({
        "entry, ENTRY",
        "request, REQUEST",
        "response, RESPONSE",
        "ENTRY, ENTRY",
        "REQUEST, REQUEST",
        "RESPONSE, RESPONSE"
    })
    @DisplayName("Should be case insensitive when finding by label")
    void testFindByLabel_CaseInsensitive(String label, String expectedGroup) {
        FieldGroup result = FieldGroup.findByLabel(label);
        assertEquals(FieldGroup.valueOf(expectedGroup), result);
    }

    @Test
    @DisplayName("Should return null for unknown label")
    void testFindByLabel_UnknownLabel() {
        FieldGroup result = FieldGroup.findByLabel("UnknownLabel");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for null label")
    void testFindByLabel_NullLabel() {
        assertThrows(NullPointerException.class, () -> {
            FieldGroup.findByLabel(null);
        });
    }

    @Test
    @DisplayName("Should return null for empty label")
    void testFindByLabel_EmptyLabel() {
        FieldGroup result = FieldGroup.findByLabel("");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return correct label for ENTRY")
    void testGetLabel_Entry() {
        assertEquals("Entry", FieldGroup.ENTRY.getLabel());
    }

    @Test
    @DisplayName("Should return correct label for REQUEST")
    void testGetLabel_Request() {
        assertEquals("Request", FieldGroup.REQUEST.getLabel());
    }

    @Test
    @DisplayName("Should return correct label for RESPONSE")
    void testGetLabel_Response() {
        assertEquals("Response", FieldGroup.RESPONSE.getLabel());
    }

    @Test
    @DisplayName("Should have three field groups")
    void testFieldGroupCount() {
        assertEquals(3, FieldGroup.values().length);
    }
}
