package prozed.io.example.controller;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.PostRequest;
import prozed.io.example.model.User;
import prozed.io.example.service.UserService;

@Bean
@Controller(path = "/")
public class UserController {

    @Inject
    private UserService userService;

    @GetRequest(value = "/user/{id}")
    public User getUser(@PathParam("{id}") int id) {
        return userService.getUser(id).orElseThrow();
    }

    @PostRequest(value = "/user")
    public int createUser(@PayloadParam User user) {
        return userService.createUser(user);
    }

    @GetRequest(value = "/health", produces = ContentType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
