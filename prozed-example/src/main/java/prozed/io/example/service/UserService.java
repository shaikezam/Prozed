package prozed.io.example.service;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.example.repository.UserRepository;

import java.util.*;

@Bean
public class UserService {

    @Inject
    private UserRepository userRepository;

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(userRepository.getUser(id));
    }

    public int createUser(User user) {
        Objects.requireNonNull(user, "User must not be null");

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
