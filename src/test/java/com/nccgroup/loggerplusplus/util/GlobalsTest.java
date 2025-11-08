package com.nccgroup.loggerplusplus.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Globals utility class
 */
@DisplayName("Globals Tests")
class GlobalsTest {

    @Test
    @DisplayName("LOG_ENTRY_ID_PATTERN should match valid log entry IDs")
    void testLogEntryIdPattern_ValidIds() {
        Pattern pattern = Globals.LOG_ENTRY_ID_PATTERN;

        Matcher matcher = pattern.matcher("$LPP:123$");
        assertTrue(matcher.find());
        assertEquals("123", matcher.group(1));
    }

    @ParameterizedTest
    @CsvSource({
        "$LPP:1$, 1",
        "$LPP:42$, 42",
        "$LPP:999$, 999",
        "$LPP:1234567890$, 1234567890"
    })
    @DisplayName("LOG_ENTRY_ID_PATTERN should extract ID from various formats")
    void testLogEntryIdPattern_ExtractIds(String input, String expectedId) {
        Matcher matcher = Globals.LOG_ENTRY_ID_PATTERN.matcher(input);
        assertTrue(matcher.find(), "Pattern should match: " + input);
        assertEquals(expectedId, matcher.group(1), "Should extract correct ID");
    }

    @Test
    @DisplayName("LOG_ENTRY_ID_PATTERN should match ID in middle of text")
    void testLogEntryIdPattern_InText() {
        String text = "Some text before $LPP:456$ and after";
        Matcher matcher = Globals.LOG_ENTRY_ID_PATTERN.matcher(text);
        assertTrue(matcher.find());
        assertEquals("456", matcher.group(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "$LPP:$",           // No ID
        "$LPP:abc$",        // Non-numeric ID
        "LPP:123",          // Missing delimiters
        "$LPP:123",         // Missing closing delimiter
        "LPP:123$",         // Missing opening delimiter
        "$LPP:-123$"        // Negative number
    })
    @DisplayName("LOG_ENTRY_ID_PATTERN should not match invalid formats")
    void testLogEntryIdPattern_InvalidFormats(String input) {
        Matcher matcher = Globals.LOG_ENTRY_ID_PATTERN.matcher(input);
        assertFalse(matcher.find(), "Should not match invalid format: " + input);
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should match basic HTML title")
    void testHtmlTitlePattern_BasicTitle() {
        String html = "<title>Test Page</title>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertTrue(matcher.find());
        assertEquals("Test Page", matcher.group(1));
    }

    @ParameterizedTest
    @CsvSource({
        "'<title>Simple Title</title>', 'Simple Title'",
        "'<TITLE>Uppercase Title</TITLE>', 'Uppercase Title'",
        "'<TiTlE>Mixed Case</TiTlE>', 'Mixed Case'",
        "'<title>Title with numbers 123</title>', 'Title with numbers 123'",
        "'<title>Special chars !@#$%</title>', 'Special chars !@#$%'"
    })
    @DisplayName("HTML_TITLE_PATTERN should extract titles from various formats")
    void testHtmlTitlePattern_VariousFormats(String html, String expectedTitle) {
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertTrue(matcher.find(), "Pattern should match: " + html);
        assertEquals(expectedTitle, matcher.group(1), "Should extract correct title");
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should be case insensitive")
    void testHtmlTitlePattern_CaseInsensitive() {
        String[] testCases = {
            "<title>Test</title>",
            "<TITLE>Test</TITLE>",
            "<Title>Test</Title>",
            "<TiTlE>Test</TiTlE>"
        };

        for (String html : testCases) {
            Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
            assertTrue(matcher.find(), "Should match: " + html);
            assertEquals("Test", matcher.group(1));
        }
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should match title in HTML document")
    void testHtmlTitlePattern_InDocument() {
        String html = "<html><head><title>My Page Title</title></head><body>Content</body></html>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertTrue(matcher.find());
        assertEquals("My Page Title", matcher.group(1));
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should match first title only")
    void testHtmlTitlePattern_FirstTitleOnly() {
        String html = "<title>First Title</title><title>Second Title</title>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertTrue(matcher.find());
        assertEquals("First Title", matcher.group(1));
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should handle empty title")
    void testHtmlTitlePattern_EmptyTitle() {
        String html = "<title></title>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        // The pattern requires at least one character (.+?)
        assertFalse(matcher.find());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<title>",           // Unclosed tag
        "</title>",          // Only closing tag
        "title>Test</title", // Missing angle brackets
        "<titl>Test</titl>", // Wrong tag name
        "<div>Not a title</div>"
    })
    @DisplayName("HTML_TITLE_PATTERN should not match invalid HTML")
    void testHtmlTitlePattern_InvalidHtml(String html) {
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertFalse(matcher.find(), "Should not match invalid HTML: " + html);
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should NOT match title with newlines (no DOTALL flag)")
    void testHtmlTitlePattern_DoesNotMatchNewlines() {
        // The pattern does not have Pattern.DOTALL, so . does not match \n
        String html = "<title>Title with\nnewlines</title>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);
        assertFalse(matcher.find(), "Pattern should not match titles containing newlines");
    }

    @Test
    @DisplayName("HTML_TITLE_PATTERN should use non-greedy matching")
    void testHtmlTitlePattern_NonGreedy() {
        String html = "<title>Title One</title> Some text <title>Title Two</title>";
        Matcher matcher = Globals.HTML_TITLE_PATTERN.matcher(html);

        // First match
        assertTrue(matcher.find());
        assertEquals("Title One", matcher.group(1));

        // Second match
        assertTrue(matcher.find());
        assertEquals("Title Two", matcher.group(1));
    }

    @Test
    @DisplayName("Should have valid version format")
    void testVersion() {
        assertNotNull(Globals.VERSION);
        assertFalse(Globals.VERSION.isEmpty());
        // Version should be in format X.Y.Z
        assertTrue(Globals.VERSION.matches("\\d+\\.\\d+\\.\\d+"),
            "Version should be in format X.Y.Z");
    }

    @Test
    @DisplayName("Should have valid app name")
    void testAppName() {
        assertEquals("Logger++", Globals.APP_NAME);
    }

    @Test
    @DisplayName("Should have non-null constant values")
    void testConstantsNotNull() {
        assertNotNull(Globals.APP_NAME);
        assertNotNull(Globals.VERSION);
        assertNotNull(Globals.AUTHOR);
        assertNotNull(Globals.GITHUB_URL);
        assertNotNull(Globals.LOG_ENTRY_ID_PATTERN);
        assertNotNull(Globals.HTML_TITLE_PATTERN);
    }

    @Test
    @DisplayName("ElasticAuthType enum should have three values")
    void testElasticAuthType() {
        assertEquals(3, Globals.ElasticAuthType.values().length);
        assertNotNull(Globals.ElasticAuthType.ApiKey);
        assertNotNull(Globals.ElasticAuthType.Basic);
        assertNotNull(Globals.ElasticAuthType.None);
    }

    @Test
    @DisplayName("Protocol enum should have HTTP and HTTPS")
    void testProtocolEnum() {
        assertEquals(2, Globals.Protocol.values().length);
        assertNotNull(Globals.Protocol.HTTP);
        assertNotNull(Globals.Protocol.HTTPS);
    }
}
