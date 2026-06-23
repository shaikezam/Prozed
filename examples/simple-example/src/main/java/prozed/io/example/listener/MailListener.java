package prozed.io.example.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.api.Listener;

@Bean
@Listener(destination = "mail", destinationType = DestinationType.QUEUE)
public class MailListener implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailListener.class);

    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("Message received from queue: {}", message.getBody(String.class));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
