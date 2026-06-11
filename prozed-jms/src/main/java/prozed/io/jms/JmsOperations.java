package prozed.io.jms;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
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

    public void sendMessage(String message, String destination, DestinationType destinationType) {
        try (JMSContext context = jmsRegistry.getConnectionFactory().createContext()) {
            Queue queue = context.createQueue(destination);
            JMSProducer producer = context.createProducer();
            producer.send(queue, "Hello from Prozed");
            LOGGER.debug("Message sent.");
        }
    }
}
