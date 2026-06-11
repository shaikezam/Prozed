package prozed.io.jms;

import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.internal.JmsRegistry;

@Bean
public class JmsOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsOperations.class);

    @Inject
    private JmsRegistry jmsRegistry;

    public void sendMessage(String message, String prozedDestination, DestinationType destinationType) {
        try (JMSContext context = jmsRegistry.getConnectionFactory().createContext()) {
            Destination destination = switch (destinationType) {
                case QUEUE -> context.createQueue(prozedDestination);
                case TOPIC -> context.createTopic(prozedDestination);
            };
            JMSProducer producer = context.createProducer();
            producer.send(destination, "Hello from Prozed");
            LOGGER.debug("Message sent.");
        }
    }
}
