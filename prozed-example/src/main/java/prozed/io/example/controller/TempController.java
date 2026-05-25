package prozed.io.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.web.*;
import prozed.io.core.internal.servlet.DispatcherServlet;

@Bean
@Controller(path = "/temp")
public class TempController {

    private final Logger logger = LoggerFactory.getLogger(TempController.class);

    @GetRequest(value = "/hello")
    public String hello() {
        return "Hello from TempController!";
    }

    @GetRequest(value = "/status")
    public String status(@QueryParam(value = "hi") String var1, @QueryParam(value = "hi2") int var2) {
        logger.info("var1: {}, var2: {}", var1, var2);
        return "{\"status\": \"ok\", \"controller\": \"TempController\"}";
    }

    @GetRequest(value = "/user")
    public User getUser() {
        return new User(1, "shaikezam");
    }

    @PostRequest(value = "/echo")
    public String echo(@PayloadParam User user) {
        return user.name();
    }

    record User(int id, String name) {
    }
}
