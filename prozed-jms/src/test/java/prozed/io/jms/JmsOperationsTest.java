package prozed.io.jms;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.jms.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.internal.JmsRegistry;
import prozed.io.jms.utils.Dummy;
import prozed.io.test.utils.RandomUtils;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JmsOperationsTest {

    @InjectMocks
    private JmsOperations jmsOperations;
    @Mock
    private JmsRegistry jmsRegistry;
    @Mock
    private ConnectionFactory connectionFactory;
    @Mock
    private JMSContext ctx;
    @Mock
    private Queue queue;
    @Mock
    private Topic topic;
    @Mock
    private JMSProducer producer;
    private static final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        when(jmsRegistry.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.createContext()).thenReturn(ctx);
        when(ctx.createProducer()).thenReturn(producer);
    }

    @Test
    void testSendRawMessage() {
        // given
        String rawMessage = RandomUtils.randomAlphbetString(10);
        String brokerDestination = RandomUtils.randomAlphbetString(10);
        DestinationType destinationType = RandomUtils.randomEnum(DestinationType.class);
        Destination destination = mockDestination(destinationType, brokerDestination);

        // when
        jmsOperations.sendRawMessage(rawMessage, brokerDestination, destinationType);

        // then
        verify(producer).send(destination, rawMessage);
    }

    @Test
    void testSendObjectMessage() {
        // given
        Dummy dummy = new Dummy
                .DummyBuilder()
                .build();
        String rawMessage = gson.toJson(dummy, Dummy.class);
        String brokerDestination = RandomUtils.randomAlphbetString(10);
        DestinationType destinationType = RandomUtils.randomEnum(DestinationType.class);
        Destination destination = mockDestination(destinationType, brokerDestination);

        // when
        jmsOperations.sendMessage(dummy, brokerDestination, destinationType);

        // then
        verify(producer).send(destination, rawMessage);
    }

    @Test
    void testSendCollectionMessage() {
        // given
        List<Dummy> dummies = List.of(new Dummy
                        .DummyBuilder()
                        .build(),
                new Dummy
                        .DummyBuilder()
                        .build());
        String rawMessage = gson.toJson(dummies, new TypeToken<Collection<Dummy>>() {
        }.getType());
        String brokerDestination = RandomUtils.randomAlphbetString(10);
        DestinationType destinationType = RandomUtils.randomEnum(DestinationType.class);
        Destination destination = mockDestination(destinationType, brokerDestination);

        // when
        jmsOperations.sendMessage(dummies, brokerDestination, destinationType);

        // then
        verify(producer).send(destination, rawMessage);
    }

    private Destination mockDestination(DestinationType destinationType, String brokerDestination) {
        return switch (destinationType) {
            case TOPIC -> {
                when(ctx.createTopic(brokerDestination)).thenReturn(topic);
                yield topic;
            }
            case QUEUE -> {
                when(ctx.createQueue(brokerDestination)).thenReturn(queue);
                yield queue;
            }
        };
    }


}