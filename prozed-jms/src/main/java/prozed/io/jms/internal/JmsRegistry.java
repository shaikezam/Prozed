package prozed.io.jms.internal;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.web.ProzedServer;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.core.internal.reflection.PackageScanner;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.api.Listener;

import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static prozed.io.core.internal.properties.Constants.WEB_SERVICE_SCAN_PACKAGE;
import static prozed.io.jms.utils.Constants.*;

@Bean
public class JmsRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsRegistry.class);
    private final List<Connection> connections = new ArrayList<>();
    private final JmsPoolConnectionFactory connectionFactory;
    private final ActiveMQConnectionFactory durableConnectionFactory;
    private final PackageScanner packageScanner = new PackageScanner();

    public JmsRegistry() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                ProzedPropertiesWrapper.getProperty(JMS_USERNAME),
                ProzedPropertiesWrapper.getProperty(JMS_PASSWORD),
                ProzedPropertiesWrapper.getProperty(JMS_BROKER_URL)
        );
        this.durableConnectionFactory = activeMQConnectionFactory;
        RedeliveryPolicy redeliveryPolicy = activeMQConnectionFactory.getRedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(
                ProzedPropertiesWrapper.getProperty(JMS_REDELIVERY_MAX_REDELIVERIES, DEFAULT_JMS_REDELIVERY_MAX_REDELIVERIES)));
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(
                ProzedPropertiesWrapper.getProperty(JMS_REDELIVERY_INITIAL_DELAY, DEFAULT_JMS_REDELIVERY_INITIAL_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(
                ProzedPropertiesWrapper.getProperty(JMS_REDELIVERY_BACKOFF_MULTIPLIER, DEFAULT_JMS_REDELIVERY_BACKOFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                ProzedPropertiesWrapper.getProperty(JMS_REDELIVERY_EXPONENTIAL_BACKOFF, DEFAULT_JMS_REDELIVERY_EXPONENTIAL_BACKOFF)));
        this.connectionFactory = new JmsPoolConnectionFactory();
        this.connectionFactory.setConnectionFactory(activeMQConnectionFactory);
        this.connectionFactory.setMaxConnections(Integer.parseInt(
                ProzedPropertiesWrapper.getProperty(JMS_POOL_MAX_CONNECTIONS, DEFAULT_JMS_POOL_MAX_CONNECTIONS)));
        this.connectionFactory.setMaxSessionsPerConnection(Integer.parseInt(
                ProzedPropertiesWrapper.getProperty(JMS_POOL_MAX_SESSIONS_PER_CONNECTION, DEFAULT_JMS_POOL_MAX_SESSIONS_PER_CONNECTION)));
        this.connectionFactory.setConnectionIdleTimeout(Integer.parseInt(
                ProzedPropertiesWrapper.getProperty(JMS_POOL_IDLE_TIMEOUT, DEFAULT_JMS_POOL_IDLE_TIMEOUT)));
    }

    // test-only seam: bypass property-driven bootstrap, inject factories directly
    JmsRegistry(JmsPoolConnectionFactory connectionFactory, ActiveMQConnectionFactory durableConnectionFactory) {
        this.connectionFactory = connectionFactory;
        this.durableConnectionFactory = durableConnectionFactory;
    }

    public void postInit() {
        LOGGER.info("Initializing JmsRegistry");
        ProzedContainer prozedContainer = ProzedServer.getContainer();
        String packageToScan = ProzedPropertiesWrapper.getProperty(WEB_SERVICE_SCAN_PACKAGE);
        Set<Class<?>> listeners = packageScanner.scan(packageToScan, Listener.class);
        listeners.forEach(listener -> {
            Listener annotation = listener.getAnnotation(Listener.class);
            Object listenerInstance = prozedContainer.get(listener);

            if (!(listenerInstance instanceof MessageListener)) {
                throw new IllegalStateException(
                        "Listener " + listener.getName() + " must be marked with @Bean and implement jakarta.jms.MessageListener"
                );
            }

            try {
                this.registerListener(listenerInstance, annotation.destination(), annotation.destinationType(),
                        annotation.durable(), annotation.subscriptionName());
            } catch (Exception e) {
                throw new RuntimeException("Failed to register listener " + listener.getName(), e);
            }
        });
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    void registerListener(Object listener, String prozedDestination, DestinationType destinationType,
                           boolean durable, String subscriptionName) throws Exception {
        if (durable && destinationType != DestinationType.TOPIC) {
            throw new IllegalStateException("Durable subscriptions are only supported for TOPIC destinations.");
        }
        if (durable && subscriptionName.isBlank()) {
            throw new IllegalStateException("Durable listener on destination " + prozedDestination
                    + " must declare a subscriptionName.");
        }

        Connection conn = durable ? durableConnectionFactory.createConnection() : connectionFactory.createConnection();
        if (durable) {
            String baseClientId = ProzedPropertiesWrapper.getProperty(JMS_CLIENT_ID);
            conn.setClientID(baseClientId + "-" + subscriptionName);
        }
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = switch (destinationType) {
            case QUEUE -> session.createQueue(prozedDestination);
            case TOPIC -> session.createTopic(prozedDestination);
        };
        MessageConsumer consumer = durable
                ? session.createDurableConsumer((Topic) destination, subscriptionName)
                : session.createConsumer(destination);
        consumer.setMessageListener((MessageListener) listener);
        conn.start();
        connections.add(conn);
    }

    public void preDestroy() {
        LOGGER.info("Destroying JmsRegistry");
        try {
            for (Connection connection : connections) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    // ignore
                }
            }
        } finally {
            connectionFactory.stop();
        }
    }

}
