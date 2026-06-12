package prozed.io.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.example.repository.UserRepository;
import prozed.io.jms.JmsOperations;
import prozed.io.jms.api.DestinationType;

import java.util.*;

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
        jmsOperations.sendRawMessage("%s user create".formatted(user), "mail", DestinationType.QUEUE);
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
}
