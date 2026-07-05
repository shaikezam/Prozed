package prozed.io.example.scheduling;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.scheduling.SchedulerContainer;
import prozed.io.core.api.scheduling.SchedulingTaskProperties;
import prozed.io.example.repository.HeartbeatRepository;

import java.util.concurrent.TimeUnit;

@Bean
public class HeartbeatScheduler {

    @Inject
    private SchedulerContainer schedulerContainer;
    @Inject
    private HeartbeatRepository heartbeatRepository;

    public void postInit() {
        schedulerContainer.register(new SchedulingTaskProperties(
                "heartbeat",
                200,
                TimeUnit.MILLISECONDS,
                heartbeatRepository::tick));
    }
}
