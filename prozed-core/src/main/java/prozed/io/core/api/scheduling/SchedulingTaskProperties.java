package prozed.io.core.api.scheduling;

import java.util.concurrent.TimeUnit;

public record SchedulingTaskProperties(String taskName,
                                       long initialDelay,
                                       TimeUnit initialDelayUnit,
                                       long interval,
                                       TimeUnit intervalUnit,
                                       long timeout,
                                       TimeUnit timeoutUnit,
                                       Runnable method) {


    public SchedulingTaskProperties(String taskName,
                                    long initialDelay, TimeUnit initialDelayUnit,
                                    long interval, TimeUnit intervalUnit,
                                    Runnable method) {
        this(taskName, initialDelay, initialDelayUnit, interval, intervalUnit, 0, TimeUnit.MILLISECONDS, method);
    }
    
    public SchedulingTaskProperties(String taskName, long interval, TimeUnit intervalUnit, Runnable method) {
        this(taskName, interval, intervalUnit, interval, intervalUnit, 0, TimeUnit.MILLISECONDS, method);
    }
}
