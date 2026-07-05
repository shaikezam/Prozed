package prozed.io.core.internal.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultCronScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCronScheduler.class);
    private static final long SHUTDOWN_DRAIN_TIMEOUT_SECONDS = 10;

    private final String name;
    private final long initialDelay;
    private final TimeUnit initialDelayUnit;
    private final long interval;
    private final TimeUnit intervalUnit;
    private final long timeout;               // 0 = no timeout
    private final TimeUnit timeoutUnit;
    private final ScheduledExecutorService timer;
    private final ExecutorService virtualExecutor;
    private final Runnable method;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> future;
    private volatile Future<?> currentExecution;

    public DefaultCronScheduler(
            String name,
            long initialDelay,
            TimeUnit initialDelayUnit,
            long interval,
            TimeUnit intervalUnit,
            long timeout,
            TimeUnit timeoutUnit,
            ScheduledExecutorService timer,
            ExecutorService virtualExecutor,
            Runnable method) {
        this.name = name;
        this.initialDelay = initialDelay;
        this.initialDelayUnit = initialDelayUnit;
        this.interval = interval;
        this.intervalUnit = intervalUnit;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.timer = timer;
        this.virtualExecutor = virtualExecutor;
        this.method = method;
    }

    public void start() {
        if (future == null) {
            // Normalize to nanos so initialDelay and interval may use different units.
            future = timer.scheduleAtFixedRate(
                    this::trigger,
                    initialDelayUnit.toNanos(initialDelay),
                    intervalUnit.toNanos(interval),
                    TimeUnit.NANOSECONDS);
            LOGGER.info("Scheduled task '{}' started (initialDelay {} {}, interval {} {}, timeout {} {})",
                    name, initialDelay, initialDelayUnit, interval, intervalUnit,
                    timeout > 0 ? timeout : "no", timeoutUnit);
        }
    }

    private void trigger() {
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("Task '{}' previous execution still running — skipping this tick", name);
            return;
        }
        Future<?> execution = virtualExecutor.submit(() -> {
            try {
                LOGGER.debug("Executing scheduled task: {}", name);
                method.run();
            } catch (Exception e) {
                LOGGER.error("Error in scheduled task '{}': ", name, e);
            } finally {
                running.set(false);
            }
        });
        currentExecution = execution;

        if (timeout > 0) {
            enforceTimeout(execution);
        }
    }

    /**
     * Schedules a one-shot watchdog on the timer thread. When the timeout fires,
     * if the execution is still in flight, interrupt it. The task will only stop
     * if the code honors the interrupt (JDBC queryTimeout, Thread.interrupted(),
     * blocking I/O with InterruptedException).
     */
    private void enforceTimeout(Future<?> execution) {
        timer.schedule(() -> {
            if (!execution.isDone()) {
                LOGGER.warn("Task '{}' exceeded timeout of {} {} — interrupting",
                        name, timeout, timeoutUnit);
                execution.cancel(true);   // interrupt the virtual thread
            }
        }, timeout, timeoutUnit);
    }

    public void stop() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);   // no new ticks are scheduled from here on
            LOGGER.info("Scheduled task '{}' stopped", name);
        }
        future = null;   // allow start() to re-arm this task (pause/resume)
        drainCurrentExecution();
    }

    /**
     * Wait for an in-flight execution to finish so callers (shutdown) can rely on no
     * task still touching shared resources once stop() returns. Bounded wait; if the
     * task overruns it is interrupted, same contract as the timeout watchdog.
     */
    private void drainCurrentExecution() {
        Future<?> execution = currentExecution;
        if (execution == null || execution.isDone()) {
            return;
        }
        try {
            execution.get(SHUTDOWN_DRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            LOGGER.warn("Task '{}' still running after {}s on stop — interrupting",
                    name, SHUTDOWN_DRAIN_TIMEOUT_SECONDS);
            execution.cancel(true);
        } catch (InterruptedException e) {
            execution.cancel(true);
            Thread.currentThread().interrupt();
        } catch (ExecutionException ignored) {
            // task threw; already logged inside the run wrapper
        }
    }
}