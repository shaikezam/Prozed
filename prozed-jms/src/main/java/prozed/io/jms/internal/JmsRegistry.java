package prozed.io.jms.internal;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.core.internal.reflection.PackageScanner;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.api.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static prozed.io.core.internal.properties.Constants.WEB_SERVICE_SCAN_PACKAGE;
import static prozed.io.jms.utils.Constants.*;

@Bean
public class JmsRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsRegistry.class);
    private static final String SERVER_CLASS = "prozed.io.core.api.web.ProzedServer";
    private final List<Connection> connections = new ArrayList<>();
    private final ConnectionFactory connectionFactory;
    private final PackageScanner packageScanner = new PackageScanner();

    public JmsRegistry() {
        this.connectionFactory = new ActiveMQConnectionFactory(
                ProzedPropertiesWrapper.getProperty(JMS_USERNAME),
                ProzedPropertiesWrapper.getProperty(JMS_PASSWORD),
                ProzedPropertiesWrapper.getProperty(JMS_BROKER_URL)
        );
    }

    public void postInit() throws Exception {
        LOGGER.info("Initializing JmsRegistry");
        Class<?> prozedServerClass = Class.forName(SERVER_CLASS);
        Method getContainerMethod = prozedServerClass.getMethod("getContainer");
        ProzedContainer prozedContainer = (ProzedContainer) getContainerMethod.invoke(null);
        String packageToScan = ProzedPropertiesWrapper.getProperty(WEB_SERVICE_SCAN_PACKAGE);
        Set<Class<?>> listeners = packageScanner.scan(packageToScan, Listener.class);
        listeners.forEach(listener -> {
            Listener annotation = listener.getAnnotation(Listener.class);
            try {
                this.registerListener(prozedContainer.get(listener), annotation.destination(), annotation.destinationType());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    private void registerListener(Object listener, String prozedDestination, DestinationType destinationType) throws Exception {
        Connection conn = connectionFactory.createConnection();
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = switch (destinationType) {
            case QUEUE -> session.createQueue(prozedDestination);
            case TOPIC -> session.createTopic(prozedDestination);
        };
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener((MessageListener) listener);
        conn.start();
        connections.add(conn);
    }

    public void preDestroy() {
        LOGGER.info("Destroying JmsRegistry");
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (JMSException e) {
                // ignore
            }
        }
    }

}
