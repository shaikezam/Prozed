package prozed.io.example.controller;

import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PostRequest;
import prozed.io.core.api.web.Produces;

@Controller(path = "/temp")
public class TempController {

    @GetRequest(value = "/hello")
    @Produces("text/plain")
    public String hello() {
        return "Hello from TempController!";
    }

    @GetRequest(value = "/status")
    @Produces("application/json")
    public String status() {
        return "{\"status\": \"ok\", \"controller\": \"TempController\"}";
    }

    @PostRequest(value = "/echo")
    @Produces("application/json")
    public String echo(String message) {
        return "{\"echo\": \"" + message + "\"}";
    }
}
