package prozed.io.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.PostRequest;
import prozed.io.core.api.web.QueryParam;

@Bean
@Controller(path = "/temp")
public class TempController {

    private final Logger logger = LoggerFactory.getLogger(TempController.class);

    @GetRequest(value = "/hello", produces = ContentType.TEXT_PLAIN)
    public String hello() {
        return "Hello from TempController!";
    }

    @GetRequest(value = "/number", produces = ContentType.TEXT_PLAIN)
    public int number() {
        return 42;
    }

    @GetRequest(value = "/enabled", produces = ContentType.TEXT_PLAIN)
    public boolean enabled() {
        return true;
    }

    @GetRequest(value = "/status", produces = ContentType.TEXT_PLAIN)
    public String status(@QueryParam(value = "hi") String var1, @QueryParam(value = "hi2") int var2) {
        logger.info("var1: {}, var2: {}", var1, var2);
        return "{\"status\": \"ok\", \"controller\": \"TempController\"}";
    }

    @GetRequest(value = "/user")
    public User getUser() {
        return new User(1, "shaikezam");
    }

    @PostRequest(value = "/echo", produces = ContentType.TEXT_PLAIN)
    public String echo(@PayloadParam User user) {
        return user.name();
    }

    public record User(int id, String name) {
    }
}
