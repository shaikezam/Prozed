package prozed.io.core.internal.properties;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static prozed.io.core.internal.properties.Constants.*;

public class ProzedPropertiesWrapper {
    private static final Properties PROPERTIES = new Properties();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");

    static {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(PROZED_PROPERTIES)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found %s".formatted(PROZED_PROPERTIES));
            }
            PROPERTIES.load(in);
            resolvePlaceholders();
        } catch (Exception e) {
            throw new IllegalStateException("Prozed: failed to read %s file".formatted(PROZED_PROPERTIES), e);
        }
    }

    /**
     * Replaces {@code ${VAR}} and {@code ${VAR:default}} placeholders in every property value
     * with the matching environment variable (fed by Docker/Compose args). When the variable is
     * unset, the inline default is used; with no default the placeholder resolves to an empty string.
     */
    private static void resolvePlaceholders() {
        for (String key : PROPERTIES.stringPropertyNames()) {
            PROPERTIES.setProperty(key, resolve(PROPERTIES.getProperty(key)));
        }
    }

    static String resolve(String value) {
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            String defaultValue = matcher.group(2);
            String resolved = System.getenv(name);
            if (resolved == null) {
                resolved = defaultValue != null ? defaultValue : "";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(resolved));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property %s not found".formatted(key));
        }

        return value;
    }

    public static int getServicePort() {
        return Integer.parseInt(getProperty(WEBSERVICE_PORT, "8080"));
    }

    public static String getScanPackage() {
        return PROPERTIES.getProperty(WEB_SERVICE_SCAN_PACKAGE);
    }
}
