package prozed.io.example.service;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.example.repository.UserRepository;

import java.util.*;

@Bean
public class UserService {

    @Inject
    private UserRepository userRepositoryV2;

    private final Map<Integer, User> userRepository = new HashMap<>();

    public UserService() {
        userRepository.put(1, new User(1, "User"));
    }

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(userRepositoryV2.getUser(id));
    }

    public int createUser(User user) {
        Objects.requireNonNull(user, "User must not be null");

        return userRepositoryV2.createUsers(user);
    }

    public void createUsers(List<User> users) {
        userRepositoryV2.createUsers(users);
    }
}
