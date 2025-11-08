package com.nccgroup.loggerplusplus.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Internationalization utility class for Logger++
 * Manages resource bundles and provides access to localized strings
 */
public class Messages {
    private static final String BUNDLE_NAME = "i18n.messages";
    private static ResourceBundle resourceBundle;
    private static Locale currentLocale;

    static {
        // Initialize with default locale (English)
        currentLocale = Locale.ENGLISH;
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }

    /**
     * Gets a localized string for the given key
     * @param key The resource bundle key
     * @return The localized string, or the key itself if not found
     */
    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            // Return the key itself if translation is missing
            return '!' + key + '!';
        }
    }

    /**
     * Gets a localized string with parameter substitution
     * @param key The resource bundle key
     * @param args Arguments to substitute into the message
     * @return The formatted localized string
     */
    public static String getString(String key, Object... args) {
        try {
            String pattern = resourceBundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Sets the current locale and reloads the resource bundle
     * @param locale The new locale to use
     * @return true if the locale was successfully changed, false otherwise
     */
    public static boolean setLocale(Locale locale) {
        try {
            ResourceBundle newBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            resourceBundle = newBundle;
            currentLocale = locale;
            return true;
        } catch (MissingResourceException e) {
            // If the locale is not supported, keep the current one
            return false;
        }
    }

    /**
     * Gets the current locale
     * @return The current locale
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Gets an array of supported locales
     * @return Array of supported locales
     */
    public static Locale[] getSupportedLocales() {
        return new Locale[] {
            Locale.ENGLISH,
            new Locale("es"), // Spanish
            new Locale("fr"), // French
            new Locale("de"), // German
            new Locale("ja"), // Japanese
            Locale.SIMPLIFIED_CHINESE,
            new Locale("pt", "BR") // Portuguese (Brazil)
        };
    }
}
