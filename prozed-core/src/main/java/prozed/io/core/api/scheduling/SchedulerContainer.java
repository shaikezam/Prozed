package prozed.io.core.api.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.internal.scheduling.DefaultCronScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Central scheduler container bean.
 *
 * Platform timer triggers each tick; the work runs in a fresh virtual thread.
 * Guarantees:
 * - the same task never overlaps itself (skip-if-running)
 * - a stuck execution is interrupted after its timeout
 * - preDestroy waits briefly for in-flight work (open transactions) to finish
 */
@Bean
public class SchedulerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerContainer.class);

    private final List<DefaultCronScheduler> schedulers = new ArrayList<>();
    private final ScheduledExecutorService timer;
    private final ExecutorService virtualExecutor;

    public SchedulerContainer() {
        this.timer = Executors.newScheduledThreadPool(2);
        this.virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        LOGGER.info("SchedulerContainer created");
    }

    /**
     * Register and start a task. The first run happens after {@code initialDelay}, then
     * every {@code interval}. Pass {@code timeout} 0 for no timeout.
     */
    public void register(SchedulingTaskProperties schedulingTaskProperties) {

        DefaultCronScheduler scheduler = new DefaultCronScheduler(
                schedulingTaskProperties.taskName(),
                schedulingTaskProperties.initialDelay(),
                schedulingTaskProperties.initialDelayUnit(),
                schedulingTaskProperties.interval(),
                schedulingTaskProperties.intervalUnit(),
                schedulingTaskProperties.timeout(),
                schedulingTaskProperties.timeoutUnit(),
                timer,
                virtualExecutor,
                schedulingTaskProperties.method());
        scheduler.start();
        schedulers.add(scheduler);
        LOGGER.info("Scheduler '{}' registered and started", schedulingTaskProperties.taskName());
    }

    /**
     * Pause every registered task: cancel its timer and drain any in-flight execution,
     * but keep the executors alive so the tasks can be {@link #resume() resumed}. Once
     * this returns, no scheduler thread is running — safe to tear down resources the
     * tasks touch (e.g. a shared DB connection during a test).
     */
    public void pause() {
        for (DefaultCronScheduler scheduler : schedulers) {
            try {
                scheduler.stop();
            } catch (Exception e) {
                LOGGER.error("Error pausing scheduler", e);
            }
        }
    }

    /**
     * Re-arm every registered task after a {@link #pause()}. No-op for tasks already running.
     */
    public void resume() {
        for (DefaultCronScheduler scheduler : schedulers) {
            try {
                scheduler.start();
            } catch (Exception e) {
                LOGGER.error("Error resuming scheduler", e);
            }
        }
    }

    public void preDestroy() {
        LOGGER.info("Stopping {} scheduler(s)...", schedulers.size());

        for (DefaultCronScheduler scheduler : schedulers) {
            try {
                scheduler.stop();
            } catch (Exception e) {
                LOGGER.error("Error stopping scheduler", e);
            }
        }
        schedulers.clear();

        timer.shutdown();
        virtualExecutor.shutdown();
        try {
            if (!virtualExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                LOGGER.warn("Some tasks did not finish within 10s, forcing shutdown");
                virtualExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            virtualExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LOGGER.info("All schedulers stopped");
    }
}
