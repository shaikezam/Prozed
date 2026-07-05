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

    /**
     * No timeout (0 = run is never interrupted).
     */
    public SchedulingTaskProperties(String taskName,
                                    long initialDelay, TimeUnit initialDelayUnit,
                                    long interval, TimeUnit intervalUnit,
                                    Runnable method) {
        this(taskName, initialDelay, initialDelayUnit, interval, intervalUnit, 0, TimeUnit.MILLISECONDS, method);
    }

    /**
     * First run happens after one {@code interval}; no timeout.
     */
    public SchedulingTaskProperties(String taskName, long interval, TimeUnit intervalUnit, Runnable method) {
        this(taskName, interval, intervalUnit, interval, intervalUnit, 0, TimeUnit.MILLISECONDS, method);
    }
}
