package prozed.io.test.utils;

import java.io.InputStream;
import java.util.Properties;

public class TestPropertiesReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream("prozed.properties")) {
            if (in == null) {
                throw new IllegalStateException("prozed.properties not found in classpath");
            }
            PROPERTIES.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load prozed.properties");
        }
    }

    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property %s not found".formatted(key));
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}