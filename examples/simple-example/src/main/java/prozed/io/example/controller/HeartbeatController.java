package prozed.io.example.controller;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.GetRequest;
import prozed.io.example.repository.HeartbeatRepository;

@Bean
@Controller(path = "/")
public class HeartbeatController {

    @Inject
    private HeartbeatRepository heartbeatRepository;

    @GetRequest(value = "/heartbeat/ticks", produces = ContentType.TEXT_PLAIN)
    public int ticks() {
        return heartbeatRepository.getTicks();
    }
}
