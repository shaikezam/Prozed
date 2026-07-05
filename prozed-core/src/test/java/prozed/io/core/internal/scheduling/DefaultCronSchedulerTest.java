package prozed.io.core.internal.scheduling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.test.utils.RandomUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultCronSchedulerTest {

    private ScheduledExecutorService timer;
    private ExecutorService virtualExecutor;

    @Mock
    private Runnable mockTask;

    @BeforeEach
    void setUp() {
        timer = Executors.newScheduledThreadPool(2);
        virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @AfterEach
    void tearDown() {
        timer.shutdownNow();
        virtualExecutor.shutdownNow();
    }

    @Test
    void testStartInvokesTaskRepeatedly() {
        // given
        DefaultCronScheduler scheduler = new DefaultCronScheduler(
                RandomUtils.randomAlphbetString(10),
                10, TimeUnit.MILLISECONDS,
                10, TimeUnit.MILLISECONDS,
                0, TimeUnit.MILLISECONDS,
                timer, virtualExecutor, mockTask);

        // when
        scheduler.start();

        // then
        verify(mockTask, timeout(1000).atLeastOnce()).run();
    }

    @Test
    void testStopPreventsFurtherExecutions() throws InterruptedException {
        // given
        DefaultCronScheduler scheduler = new DefaultCronScheduler(
                RandomUtils.randomAlphbetString(10),
                10, TimeUnit.MILLISECONDS,
                10, TimeUnit.MILLISECONDS,
                0, TimeUnit.MILLISECONDS,
                timer, virtualExecutor, mockTask);
        scheduler.start();
        verify(mockTask, timeout(1000).atLeastOnce()).run();

        // when
        scheduler.stop();
        Thread.sleep(100);
        int invocationsAtStop = mockingDetails(mockTask).getInvocations().size();
        Thread.sleep(200);

        // then
        assertEquals(invocationsAtStop, mockingDetails(mockTask).getInvocations().size());
    }

    @Test
    void testSkipsTickWhenPreviousExecutionStillRunning() throws InterruptedException {
        // given
        CountDownLatch releaseTask = new CountDownLatch(1);
        Runnable slowTask = () -> {
            try {
                releaseTask.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        DefaultCronScheduler scheduler = new DefaultCronScheduler(
                RandomUtils.randomAlphbetString(10),
                10, TimeUnit.MILLISECONDS,
                10, TimeUnit.MILLISECONDS,
                0, TimeUnit.MILLISECONDS,
                timer, virtualExecutor, slowTask);

        // when
        scheduler.start();
        Thread.sleep(200); // several ticks elapse while the first execution blocks

        // then
        AtomicBoolean running = (AtomicBoolean) prozed.io.test.utils.ReflectionUtils.getField(scheduler, "running");
        assertTrue(running.get());

        releaseTask.countDown();
    }

    @Test
    void testTimeoutInterruptsLongRunningTask() throws InterruptedException {
        // given
        AtomicBoolean wasInterrupted = new AtomicBoolean(false);
        CountDownLatch taskStarted = new CountDownLatch(1);
        Runnable hangingTask = () -> {
            taskStarted.countDown();
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (System.nanoTime() < deadline) {
                if (Thread.currentThread().isInterrupted()) {
                    wasInterrupted.set(true);
                    return;
                }
            }
        };
        DefaultCronScheduler scheduler = new DefaultCronScheduler(
                RandomUtils.randomAlphbetString(10),
                10, TimeUnit.MILLISECONDS,
                50, TimeUnit.MILLISECONDS,
                10, TimeUnit.MILLISECONDS,
                timer, virtualExecutor, hangingTask);

        // when
        scheduler.start();
        taskStarted.await();
        Thread.sleep(500);

        // then
        assertTrue(wasInterrupted.get());
    }
}
