package prozed.io.example.service;

import prozed.io.core.api.di.Bean;
import prozed.io.example.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Bean
public class UserService {

    private final Map<Integer, User> userRepository = new HashMap<>();

    public UserService() {
        userRepository.put(1, new User(1, "User"));
    }

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(userRepository.get(id));
    }

    public int createUser(User user) {
        Objects.requireNonNull(user, "User must not be null");

        userRepository.putIfAbsent(user.id(), user);
        return user.id();
    }
}
