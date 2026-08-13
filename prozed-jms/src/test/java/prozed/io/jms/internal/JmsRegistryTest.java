package prozed.io.jms.internal;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.jms.api.DestinationType;
import prozed.io.test.utils.RandomUtils;

import java.lang.IllegalStateException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JmsRegistryTest {

    @Mock
    private JmsPoolConnectionFactory connectionFactory;
    @Mock
    private ActiveMQConnectionFactory durableConnectionFactory;
    @Mock
    private Connection connection;
    @Mock
    private Session session;
    @Mock
    private Topic topic;
    @Mock
    private Queue queue;
    @Mock
    private MessageConsumer consumer;

    private JmsRegistry jmsRegistry;
    private MessageListener listener;

    @BeforeEach
    void setUp() {
        jmsRegistry = new JmsRegistry(connectionFactory, durableConnectionFactory);
        listener = mock(MessageListener.class);
    }

    @Test
    void testDurableTopicListenerSetsClientIdBeforeSessionAndUsesDurableConsumer() throws Exception {
        // given
        String destinationName = RandomUtils.randomAlphbetString(10);
        String subscriptionName = RandomUtils.randomAlphbetString(10);
        when(durableConnectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(destinationName)).thenReturn(topic);
        when(session.createDurableConsumer(topic, subscriptionName)).thenReturn(consumer);

        // when
        jmsRegistry.registerListener(listener, destinationName, DestinationType.TOPIC, true, subscriptionName);

        // then
        InOrder inOrder = inOrder(connection, session, consumer);
        inOrder.verify(connection).setClientID("test-client-id-" + subscriptionName);
        inOrder.verify(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        inOrder.verify(session).createDurableConsumer(topic, subscriptionName);
        inOrder.verify(consumer).setMessageListener(listener);
        inOrder.verify(connection).start();
        verifyNoInteractions(connectionFactory);
    }

    @Test
    void testNonDurableTopicListenerNeverSetsClientId() throws Exception {
        // given
        String destinationName = RandomUtils.randomAlphbetString(10);
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(destinationName)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);

        // when
        jmsRegistry.registerListener(listener, destinationName, DestinationType.TOPIC, false, "");

        // then
        verify(connection, never()).setClientID(anyString());
        verify(session).createConsumer(topic);
        verifyNoInteractions(durableConnectionFactory);
    }

    @Test
    void testDurableQueueListenerThrows() {
        // when / then
        assertThrows(IllegalStateException.class, () ->
                jmsRegistry.registerListener(listener, RandomUtils.randomAlphbetString(10), DestinationType.QUEUE,
                        true, RandomUtils.randomAlphbetString(10)));
        verifyNoInteractions(connectionFactory, durableConnectionFactory);
    }

    @Test
    void testDurableTopicListenerWithBlankSubscriptionNameThrows() {
        // when / then
        assertThrows(IllegalStateException.class, () ->
                jmsRegistry.registerListener(listener, RandomUtils.randomAlphbetString(10), DestinationType.TOPIC,
                        true, ""));
        verifyNoInteractions(connectionFactory, durableConnectionFactory);
    }
}
