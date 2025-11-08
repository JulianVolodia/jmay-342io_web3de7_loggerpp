package com.nccgroup.loggerplusplus.logentry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogEntryField enum
 */
@DisplayName("LogEntryField Tests")
class LogEntryFieldTest {

    @Test
    @DisplayName("Should get field by label in REQUEST group")
    void testGetByLabel_Request() {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.REQUEST, "Method");
        assertEquals(LogEntryField.METHOD, result);
    }

    @Test
    @DisplayName("Should get field by label in RESPONSE group")
    void testGetByLabel_Response() {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.RESPONSE, "Status");
        assertEquals(LogEntryField.STATUS, result);
    }

    @Test
    @DisplayName("Should get field by label in ENTRY group")
    void testGetByLabel_Entry() {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.ENTRY, "Tool");
        assertEquals(LogEntryField.PROXY_TOOL, result);
    }

    @ParameterizedTest
    @CsvSource({
        "url, URL",
        "URL, URL",
        "uri, URL",
        "URI, URL"
    })
    @DisplayName("Should find field by alternative labels (case insensitive)")
    void testGetByLabel_AlternativeLabels(String label, String expectedField) {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.REQUEST, label);
        assertEquals(LogEntryField.valueOf(expectedField), result);
    }

    @Test
    @DisplayName("Should return null for unknown label")
    void testGetByLabel_UnknownLabel() {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.REQUEST, "NonExistentField");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for null field group")
    void testGetByLabel_NullFieldGroup() {
        LogEntryField result = LogEntryField.getByLabel(null, "Method");
        assertNull(result);
    }

    @Test
    @DisplayName("Should get field by fully qualified name")
    void testGetByFullyQualifiedName() {
        LogEntryField result = LogEntryField.getByFullyQualifiedName("Request.Method");
        assertEquals(LogEntryField.METHOD, result);
    }

    @ParameterizedTest
    @CsvSource({
        "Request.URL",
        "Request.Method",
        "Request.Path",
        "Response.Status",
        "Response.Length",
        "Entry.Tool"
    })
    @DisplayName("Should parse fully qualified names correctly")
    void testGetByFullyQualifiedName_Various(String fqn) {
        LogEntryField result = LogEntryField.getByFullyQualifiedName(fqn);
        assertNotNull(result, "Should find field for FQN: " + fqn);
    }

    @Test
    @DisplayName("Should get fields in REQUEST group")
    void testGetFieldsInGroup_Request() {
        Collection<LogEntryField> fields = LogEntryField.getFieldsInGroup(FieldGroup.REQUEST);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertTrue(fields.contains(LogEntryField.METHOD));
        assertTrue(fields.contains(LogEntryField.URL));
        assertTrue(fields.contains(LogEntryField.PATH));
    }

    @Test
    @DisplayName("Should get fields in RESPONSE group")
    void testGetFieldsInGroup_Response() {
        Collection<LogEntryField> fields = LogEntryField.getFieldsInGroup(FieldGroup.RESPONSE);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertTrue(fields.contains(LogEntryField.STATUS));
        assertTrue(fields.contains(LogEntryField.RESPONSE_LENGTH));
        assertTrue(fields.contains(LogEntryField.TITLE));
    }

    @Test
    @DisplayName("Should get fields in ENTRY group")
    void testGetFieldsInGroup_Entry() {
        Collection<LogEntryField> fields = LogEntryField.getFieldsInGroup(FieldGroup.ENTRY);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertTrue(fields.contains(LogEntryField.NUMBER));
        assertTrue(fields.contains(LogEntryField.PROXY_TOOL));
    }

    @Test
    @DisplayName("Should return correct full label")
    void testGetFullLabel() {
        String fullLabel = LogEntryField.METHOD.getFullLabel();
        assertEquals("Request.Method", fullLabel);
    }

    @Test
    @DisplayName("Should return correct full label with custom label")
    void testGetFullLabel_WithCustomLabel() {
        String fullLabel = LogEntryField.URL.getFullLabel("URI");
        assertEquals("Request.URI", fullLabel);
    }

    @Test
    @DisplayName("Should return correct field group")
    void testGetFieldGroup() {
        assertEquals(FieldGroup.REQUEST, LogEntryField.METHOD.getFieldGroup());
        assertEquals(FieldGroup.RESPONSE, LogEntryField.STATUS.getFieldGroup());
        assertEquals(FieldGroup.ENTRY, LogEntryField.PROXY_TOOL.getFieldGroup());
    }

    @Test
    @DisplayName("Should return correct type for Integer fields")
    void testGetType_Integer() {
        assertEquals(Integer.class, LogEntryField.REQUEST_LENGTH.getType());
        assertEquals(Integer.class, LogEntryField.RESPONSE_LENGTH.getType());
        assertEquals(Integer.class, LogEntryField.NUMBER.getType());
    }

    @Test
    @DisplayName("Should return correct type for String fields")
    void testGetType_String() {
        assertEquals(String.class, LogEntryField.METHOD.getType());
        assertEquals(String.class, LogEntryField.URL.getType());
        assertEquals(String.class, LogEntryField.HOSTNAME.getType());
    }

    @Test
    @DisplayName("Should return correct type for Boolean fields")
    void testGetType_Boolean() {
        assertEquals(Boolean.class, LogEntryField.ISSSL.getType());
        assertEquals(Boolean.class, LogEntryField.INSCOPE.getType());
        assertEquals(Boolean.class, LogEntryField.COMPLETE.getType());
    }

    @Test
    @DisplayName("Should return correct type for Short fields")
    void testGetType_Short() {
        assertEquals(Short.class, LogEntryField.PORT.getType());
        assertEquals(Short.class, LogEntryField.STATUS.getType());
    }

    @Test
    @DisplayName("Should have non-null description")
    void testGetDescription() {
        assertNotNull(LogEntryField.METHOD.getDescription());
        assertFalse(LogEntryField.METHOD.getDescription().isEmpty());
    }

    @Test
    @DisplayName("Should have at least one label")
    void testGetLabels() {
        String[] labels = LogEntryField.METHOD.getLabels();
        assertNotNull(labels);
        assertTrue(labels.length > 0);
    }

    @Test
    @DisplayName("Should return descriptive message with HTML formatting")
    void testGetDescriptiveMessage() {
        String message = LogEntryField.METHOD.getDescriptiveMessage();
        assertNotNull(message);
        assertTrue(message.contains("<b>"));
        assertTrue(message.contains("Field:"));
        assertTrue(message.contains("Type:"));
        assertTrue(message.contains("Description:"));
    }

    @Test
    @DisplayName("toString should return full label")
    void testToString() {
        String result = LogEntryField.METHOD.toString();
        assertEquals("Request.Method", result);
    }

    @Test
    @DisplayName("Should handle alternative labels for STATUS field")
    void testAlternativeLabels_Status() {
        LogEntryField byStatus = LogEntryField.getByLabel(FieldGroup.RESPONSE, "Status");
        LogEntryField byStatusCode = LogEntryField.getByLabel(FieldGroup.RESPONSE, "StatusCode");

        assertNotNull(byStatus);
        assertNotNull(byStatusCode);
        assertEquals(byStatus, byStatusCode, "Both labels should resolve to the same field");
    }

    @Test
    @DisplayName("Should handle alternative labels for HOSTNAME field")
    void testAlternativeLabels_Hostname() {
        LogEntryField result = LogEntryField.getByLabel(FieldGroup.REQUEST, "Hostname");
        assertEquals(LogEntryField.HOSTNAME, result);
    }

    @Test
    @DisplayName("All fields should have valid field groups")
    void testAllFieldsHaveValidGroups() {
        for (LogEntryField field : LogEntryField.values()) {
            assertNotNull(field.getFieldGroup(),
                "Field " + field.name() + " should have a field group");
        }
    }

    @Test
    @DisplayName("All fields should have at least one label")
    void testAllFieldsHaveLabels() {
        for (LogEntryField field : LogEntryField.values()) {
            assertNotNull(field.getLabels(),
                "Field " + field.name() + " should have labels");
            assertTrue(field.getLabels().length > 0,
                "Field " + field.name() + " should have at least one label");
        }
    }

    @Test
    @DisplayName("All fields should have non-empty descriptions")
    void testAllFieldsHaveDescriptions() {
        for (LogEntryField field : LogEntryField.values()) {
            assertNotNull(field.getDescription(),
                "Field " + field.name() + " should have a description");
            assertFalse(field.getDescription().isEmpty(),
                "Field " + field.name() + " description should not be empty");
        }
    }
}
