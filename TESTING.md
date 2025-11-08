# Logger++ Automated Testing Implementation

## Overview
This document describes the automated unit tests added to Logger++ to address GitHub Issue #2.

## Test Infrastructure

### Framework
- **JUnit 5 (Jupiter)** 5.10.1 - Modern testing framework
- **Mockito** 5.7.0 - Mocking framework for future integration tests
- **Gradle Test Platform** - Configured for comprehensive test execution

### Build Configuration (build.gradle)
- Added JUnit 5 dependencies
- Added Mockito for mocking support
- Configured test task with JUnit Platform
- Made Burp Suite JAR dependency conditional (only loads if file exists locally)
- Added Java toolchain configuration for Java 17
- Updated Lombok plugin to version 6.6.3 (compatible with Gradle 7.5.1)

### CI/CD Integration (.github/workflows/test.yml)
- Runs tests on both Java 17 and Java 21
- Uses fail-fast: false to run all versions independently
- Publishes test results and generates reports
- Triggers on push/PR to main, master, and develop branches

## Test Coverage

### Test Classes

#### 1. FieldGroupTest.java (12 test methods)
Tests the `FieldGroup` enum functionality:
- Finding groups by primary labels (ENTRY, REQUEST, RESPONSE)
- Finding groups by alternative labels (Log, Proxy)
- Case-insensitive label matching
- Null and empty input handling
- Label getter validation
- Enum count validation

**Key Tests:**
- `testFindByPrimaryLabel_*` - Tests primary label lookup
- `testFindByAlternativeLabels_*` - Tests alternative label aliases
- `testFindByLabel_CaseInsensitive` - Validates case insensitivity
- `testFindByLabel_UnknownLabel` - Error handling

#### 2. LogEntryFieldTest.java (27 test methods)
Tests the `LogEntryField` enum functionality:
- Field lookup by label and fully qualified names
- Alternative label resolution (e.g., "URL" vs "URI")
- Field grouping validation
- Type checking for all field types (Integer, String, Boolean, Short)
- Label and description validation
- Comprehensive metadata validation

**Key Tests:**
- `testGetByLabel_*` - Field lookup by label within groups
- `testGetByFullyQualifiedName_*` - Fully qualified name parsing
- `testGetFieldsInGroup_*` - Group membership validation
- `testGetType_*` - Type validation for different field types
- `testAlternativeLabels_*` - Alternative label resolution
- `testAllFields*` - Comprehensive validation across all fields

#### 3. GlobalsTest.java (18 test methods)
Tests utility patterns and constants in the `Globals` class:
- LOG_ENTRY_ID_PATTERN regex validation
- HTML title extraction pattern testing
- Constants validation (APP_NAME, VERSION, etc.)
- Enum validation (ElasticAuthType, Protocol)

**Key Tests:**
- `testLogEntryIdPattern_*` - Regex pattern for log entry IDs
- `testHtmlTitlePattern_*` - HTML title extraction patterns
- `testConstantsNotNull` - Constants validation
- `testVersion` - Version format validation
- `testElasticAuthType` - Enum validation
- `testProtocolEnum` - Protocol enum validation

### Test Characteristics

All tests follow these best practices:
- **Isolated**: No external dependencies or database connections
- **Fast**: Pure unit tests with no I/O operations
- **Descriptive**: Clear test names using @DisplayName annotations
- **Parameterized**: Use @ParameterizedTest with @CsvSource for multiple scenarios
- **Comprehensive**: Cover normal, edge, and error cases

## Test Execution Count

### Test Methods: 57
- FieldGroupTest: 12 methods
- LogEntryFieldTest: 27 methods
- GlobalsTest: 18 methods

### Actual Test Cases: 100+
Many test methods use `@ParameterizedTest` with multiple input values, resulting in more actual test executions than method count. For example:
- `testFindByAlternativeLabels_Entry` has 6 parameter sets = 6 test cases
- `testFindByLabel_CaseInsensitive` has 6 parameter sets = 6 test cases
- `testGetByLabel_AlternativeLabels` has 4 parameter sets = 4 test cases

## Running Tests

### Locally
```bash
./gradlew test
```

### View Test Reports
After running tests, reports are available at:
```
build/reports/tests/test/index.html
```

### CI/CD
Tests run automatically on:
- Push to main, master, or develop branches
- Pull requests to main, master, or develop branches
- Manual workflow dispatch

## Future Test Additions

Potential areas for future test coverage:
- ExporterTest - Test CSV, JSON, HAR, Elasticsearch exporters
- FilterExpressionTest - Test filter parsing and evaluation
- GrepperTest - Test grep/search functionality
- LogEntryTest - Test log entry creation and manipulation

## Notes

- Tests are designed to work in CI environments without Burp Suite JAR
- Java toolchain ensures consistent builds across Java 17 and 21
- All tests should pass on both Java versions
- Tests focus on pure logic without UI or Burp API dependencies
