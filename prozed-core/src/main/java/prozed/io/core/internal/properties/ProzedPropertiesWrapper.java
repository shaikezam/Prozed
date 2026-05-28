package prozed.io.core.internal.properties;

import java.io.InputStream;
import java.util.Properties;

import static prozed.io.core.internal.properties.Constants.*;

public class ProzedPropertiesWrapper {
    private final Properties delegate = new Properties();

    public ProzedPropertiesWrapper() {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(PROZED_PROPERTIES)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found");
            }
            delegate.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("properties file %s not found".formatted(PROZED_PROPERTIES));
        }
    }

    public String getProperty(String key, String defaultValue) {
        return delegate.getProperty(key, defaultValue);
    }

    public int getServicePort() {
        return Integer.parseInt(getProperty(WEBSERVICE_PORT, "8080"));
    }

    public String getScanPackage() {
        return delegate.getProperty(WEB_SERVICE_SCAN_PACKAGE);
    }
}
