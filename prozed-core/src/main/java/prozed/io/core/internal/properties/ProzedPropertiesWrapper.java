package prozed.io.core.internal.properties;

import java.io.InputStream;
import java.util.Properties;

import static prozed.io.core.internal.properties.Constants.*;

public class ProzedPropertiesWrapper {
    private static final Properties PROPERTIES = new Properties();

    static {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(PROZED_PROPERTIES)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found");
            }
            PROPERTIES.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("properties file %s not found".formatted(PROZED_PROPERTIES));
        }
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
