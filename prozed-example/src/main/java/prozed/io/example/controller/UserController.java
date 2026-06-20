package prozed.io.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.*;
import prozed.io.core.api.exception.HttpException;
import prozed.io.example.model.User;
import prozed.io.example.service.UserService;

import java.util.List;

@Bean
@Controller(path = "/")
public class UserController {

    @Inject
    private UserService userService;

    @GetRequest(value = "/user/{id}")
    public User getUser(@PathParam("{id}") int id) {
        return userService
                .getUser(id)
                .orElseThrow(() -> new HttpException("User not found: " + id, HttpServletResponse.SC_NOT_FOUND));
    }

    @PostRequest(value = "/user")
    public int createUser(@PayloadParam User user) {
        return userService.createUser(user);
    }

    @PostRequest(value = "/users")
    public void createUsers(@PayloadParam List<User> users) {
        userService.createUsers(users);
    }

    @PutRequest(value = "/user/{id}")
    public void updateUser(@PathParam("{id}") int id, @PayloadParam User user) {
        userService.updateUser(user);
    }

    @DeleteRequest(value = "/user/{id}")
    public void deleteUser(@PathParam("{id}") int id) {
        userService.deleteUser(id);
    }

    @GetRequest(value = "/users/search")
    public List<User> searchUsers(@QueryParam("name") String name, @QueryParam("limit") Integer limit) {
        return userService.search(name, limit);
    }

    @GetRequest(value = "/health", produces = ContentType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
