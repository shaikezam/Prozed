package prozed.io.core.api.scheduling;

import prozed.io.test.utils.RandomUtils;

import java.util.concurrent.TimeUnit;

public class SchedulingTaskPropertiesBuilder {
    private String taskName = RandomUtils.randomAlphbetString(10);
    private long initialDelay = RandomUtils.randomPositiveInt();
    private TimeUnit initialDelayUnit = RandomUtils.randomEnum(TimeUnit.class);
    private long interval = RandomUtils.randomPositiveInt();
    private TimeUnit intervalUnit = RandomUtils.randomEnum(TimeUnit.class);
    private long timeout = RandomUtils.randomPositiveInt();
    private TimeUnit timeoutUnit = RandomUtils.randomEnum(TimeUnit.class);
    private Runnable method = () -> {};

    public SchedulingTaskPropertiesBuilder() {}

    public SchedulingTaskPropertiesBuilder withTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withInitialDelayUnit(TimeUnit initialDelayUnit) {
        this.initialDelayUnit = initialDelayUnit;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withInterval(long interval) {
        this.interval = interval;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withIntervalUnit(TimeUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
        return this;
    }

    public SchedulingTaskPropertiesBuilder withMethod(Runnable method) {
        this.method = method;
        return this;
    }

    public SchedulingTaskProperties build() {
        return new SchedulingTaskProperties(
                taskName,
                initialDelay,
                initialDelayUnit,
                interval,
                intervalUnit,
                timeout,
                timeoutUnit,
                method
        );
    }
}
