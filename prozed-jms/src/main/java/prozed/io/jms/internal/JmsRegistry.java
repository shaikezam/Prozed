package prozed.io.jms.internal;

import jakarta.jms.ConnectionFactory;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.core.internal.reflection.PackageScanner;

public class JmsRegistry {
    private ConnectionFactory connectionFactory;
    private PackageScanner packageScanner;
    private ProzedPropertiesWrapper prozedPropertiesWrapper;

}
