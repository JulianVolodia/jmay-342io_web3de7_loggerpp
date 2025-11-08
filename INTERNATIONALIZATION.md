# Internationalization (i18n) Implementation

## Overview
Logger++ now supports internationalization, allowing the user interface to be displayed in multiple languages.

## Implementation Details

### Core Infrastructure
- **Messages Utility Class**: `src/main/java/com/nccgroup/loggerplusplus/i18n/Messages.java`
  - Manages ResourceBundle loading and string retrieval
  - Supports locale switching at runtime
  - Provides formatted message support with parameter substitution

### Resource Bundles
Location: `src/main/resources/i18n/`
- `messages.properties` - Default English translations
- `messages_es.properties` - Spanish translations
- Additional languages can be added by creating `messages_[locale].properties` files

### Supported Locales
Currently supported:
- English (default)
- Spanish (es)
- French (fr) - framework ready
- German (de) - framework ready
- Japanese (ja) - framework ready
- Simplified Chinese (zh_CN) - framework ready
- Portuguese Brazil (pt_BR) - framework ready

### Internationalized Components
The following UI components have been fully internationalized:

1. **PreferencesPanel** (`src/main/java/com/nccgroup/loggerplusplus/preferences/PreferencesPanel.java`)
   - Status panel
   - Log filter controls
   - Tool selection checkboxes
   - Import/export options
   - Settings panels
   - Dialog messages

2. **LoggerMenu** (`src/main/java/com/nccgroup/loggerplusplus/util/userinterface/LoggerMenu.java`)
   - Color filters menu
   - View layout options
   - Request/Response view options
   - Log level menu

3. **AboutPanel** (`src/main/java/com/nccgroup/loggerplusplus/about/AboutPanel.java`)
   - Application title and subtitle
   - Social media buttons
   - GitHub action buttons
   - Credits and version information
   - Features list
   - Acknowledgements

## Usage

### Accessing Translated Strings
```java
import com.nccgroup.loggerplusplus.i18n.Messages;

// Simple string retrieval
String title = Messages.getString("app.name");

// With parameter substitution
String status = Messages.getString("status.running", appName);
```

### Changing Locale (Runtime)
```java
import com.nccgroup.loggerplusplus.i18n.Messages;
import java.util.Locale;

// Set to Spanish
Messages.setLocale(new Locale("es"));

// Set to French
Messages.setLocale(new Locale("fr"));
```

### Getting Supported Locales
```java
Locale[] supported = Messages.getSupportedLocales();
```

## Adding New Languages

To add support for a new language:

1. Create a new properties file: `src/main/resources/i18n/messages_[locale].properties`
2. Copy the content from `messages.properties`
3. Translate all values (keep keys in English)
4. Add the locale to `Messages.getSupportedLocales()` method
5. Test the translation by setting the locale

## Adding New Translatable Strings

When adding new UI strings:

1. Add the key-value pair to all language properties files
2. Use `Messages.getString("your.key")` instead of hardcoded strings
3. For strings with parameters, use `Messages.getString("your.key", param1, param2)`

Example:
```properties
# messages.properties
dialog.confirm.delete=Are you sure you want to delete {0} items?
```

```java
String message = Messages.getString("dialog.confirm.delete", itemCount);
```

## Best Practices

1. **Keep keys semantic**: Use descriptive keys like `menu.view.horizontal` instead of generic ones
2. **Group related keys**: Use prefixes to organize keys (e.g., `about.*`, `menu.*`, `import.*`)
3. **Preserve parameter order**: When translating, ensure {0}, {1}, etc. are in the correct position for the target language
4. **Test translations**: Verify UI layout with longer translated strings
5. **Handle special characters**: Use Unicode escapes (\u0020 for space, \n for newline)

## Future Enhancements

Potential improvements for future versions:

1. **Runtime Language Switching**: Add UI to change language without restart
2. **Locale Persistence**: Save user's locale preference
3. **UI Refresh**: Implement dynamic UI updates when locale changes
4. **Column Headers**: Internationalize table column names
5. **Log Entry Fields**: Translate field descriptions
6. **Community Translations**: Set up Crowdin or similar for community contributions

## Impact

### Modified Files
- Created: `src/main/java/com/nccgroup/loggerplusplus/i18n/Messages.java`
- Created: `src/main/resources/i18n/messages.properties`
- Created: `src/main/resources/i18n/messages_es.properties`
- Modified: `src/main/java/com/nccgroup/loggerplusplus/preferences/PreferencesPanel.java`
- Modified: `src/main/java/com/nccgroup/loggerplusplus/util/userinterface/LoggerMenu.java`
- Modified: `src/main/java/com/nccgroup/loggerplusplus/about/AboutPanel.java`

### Backward Compatibility
All changes are backward compatible. Existing installations will use English (default) strings automatically.

### Performance Impact
Minimal - ResourceBundle uses efficient caching, string lookups are O(1).

## Testing

To test the implementation:

1. Build the extension: `./gradlew build`
2. Load the extension in Burp Suite
3. Verify all UI strings display correctly
4. (Optional) Test locale switching programmatically
5. Verify Spanish translations work correctly

## Resources

- Java ResourceBundle documentation: https://docs.oracle.com/javase/tutorial/i18n/resbundle/index.html
- Unicode character reference: https://www.unicode.org/charts/
- Locale codes: https://www.oracle.com/java/technologies/javase/jdk8-jre8-suported-locales.html
