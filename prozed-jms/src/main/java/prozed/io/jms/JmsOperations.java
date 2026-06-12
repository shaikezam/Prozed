package prozed.io.jms;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.internal.JmsRegistry;

import java.util.Collection;
import java.util.Objects;

@Bean
public class JmsOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsOperations.class);
    private final Gson gson = new Gson();

    @Inject
    private JmsRegistry jmsRegistry;

    public void sendRawMessage(String message, String brokerDestination, DestinationType destinationType) {
        try (JMSContext context = jmsRegistry.getConnectionFactory().createContext()) {
            Destination destination = switch (destinationType) {
                case QUEUE -> context.createQueue(brokerDestination);
                case TOPIC -> context.createTopic(brokerDestination);
            };
            JMSProducer producer = context.createProducer();
            producer.send(destination, message);
            LOGGER.debug("Message sent.");
        }
    }

    public void sendMessage(Object message, String prozedDestination, DestinationType destinationType) {
        Objects.requireNonNull(message, "Message must not be null.");
        String rawMessage = gson.toJson(message, message.getClass());
        this.sendRawMessage(rawMessage, prozedDestination, destinationType);
    }

    public void sendMessage(Collection<?> message, String prozedDestination, DestinationType destinationType) {
        Objects.requireNonNull(message, "Message must not be null.");
        String rawMessage = gson.toJson(message, new TypeToken<Collection<?>>() {
        }.getType());
        this.sendRawMessage(rawMessage, prozedDestination, destinationType);
    }
}
