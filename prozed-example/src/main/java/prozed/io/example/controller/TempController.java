package prozed.io.example.controller;

import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PostRequest;

@Controller(path = "/temp")
public class TempController {

    @GetRequest(value = "/hello")
    public String hello() {
        return "Hello from TempController!";
    }

    @GetRequest(value = "/status")
    public String status() {
        return "{\"status\": \"ok\", \"controller\": \"TempController\"}";
    }

    @PostRequest(value = "/echo")
    public String echo(String message) {
        return "{\"echo\": \"" + message + "\"}";
    }
}
