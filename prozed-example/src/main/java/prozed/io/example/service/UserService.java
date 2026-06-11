package prozed.io.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.example.repository.UserRepository;
import prozed.io.jms.JmsOperations;
import prozed.io.jms.api.DestinationType;
import prozed.io.jms.internal.JmsRegistry;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

@Bean
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserRepository userRepository;
    @Inject
    private JmsOperations jmsOperations;

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(userRepository.getUser(id));
    }

    public int createUser(User user) {
        Objects.requireNonNull(user, "User must not be null");
        jmsOperations.sendMessage("%s user create".formatted(user), "mail", DestinationType.QUEUE);
        return userRepository.createUsers(user);
    }

    public void createUsers(List<User> users) {
        userRepository.createUsers(users);
    }

    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    public void updateUser(User user) {
        Objects.requireNonNull(user, "User must not be null");
        userRepository.updateUser(user);
    }

    private void sendMessagesAsync(User user, int userId) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int messagesPerThread = 10000 / 5;

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                for (int j = 0; j < messagesPerThread; j++) {
                    String message = "User %d created: %s - Message %d"
                            .formatted(userId, user.name(), j);
                    try {
                        jmsOperations.sendMessage(message, "mail", DestinationType.QUEUE);
                    } catch (Exception e) {
                        LOGGER.error("Failed to send message", e);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            // block until all 10000 messages are sent
            executor.awaitTermination(5, MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Message sending interrupted", e);
        }
    }
}
