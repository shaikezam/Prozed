package prozed.io.core.api.scheduling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.test.utils.RandomUtils;
import prozed.io.test.utils.ReflectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerContainerTest {

    private SchedulerContainer schedulerContainer;

    @Mock
    private Runnable mockTask;

    @BeforeEach
    void setUp() {
        schedulerContainer = new SchedulerContainer();
    }

    @AfterEach
    void tearDown() {
        schedulerContainer.preDestroy();
    }

    @Test
    void testRegisterTaskWithTimeout() {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withInitialDelay(10)
                .withInitialDelayUnit(TimeUnit.MILLISECONDS)
                .withInterval(10)
                .withIntervalUnit(TimeUnit.MILLISECONDS)
                .withMethod(mockTask)
                .build();

        // when
        schedulerContainer.register(properties);

        // then
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(1, schedulers.size());
        verify(mockTask, timeout(1000).atLeastOnce()).run();
    }

    @Test
    void testRegisterTaskWithoutTimeout() {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withTimeout(0)
                .withMethod(mockTask)
                .build();

        // when
        schedulerContainer.register(properties);

        // then
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(1, schedulers.size());
    }

    @Test
    void testRegisterTaskUsingCompactConstructor() {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withMethod(mockTask)
                .build();

        // when
        schedulerContainer.register(properties);

        // then
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(1, schedulers.size());
    }

    @Test
    void testPreDestroyWithoutTasks() {
        // given & when
        schedulerContainer.preDestroy();

        // then
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(0, schedulers.size());
    }

    @Test
    void testPreDestroyAfterRegisteringTask() {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withMethod(mockTask)
                .build();
        schedulerContainer.register(properties);
        List<?> schedulersAfterRegister = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(1, schedulersAfterRegister.size());

        // when
        schedulerContainer.preDestroy();

        // then
        List<?> schedulersAfterDestroy = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(0, schedulersAfterDestroy.size());
    }

    @Test
    void testRegisterMultipleTasks() {
        // given
        SchedulingTaskProperties task1 = new SchedulingTaskPropertiesBuilder()
                .withMethod(mockTask)
                .build();

        SchedulingTaskProperties task2 = new SchedulingTaskPropertiesBuilder()
                .withTimeout(RandomUtils.randomLong())
                .withMethod(mockTask)
                .build();

        SchedulingTaskProperties task3 = new SchedulingTaskPropertiesBuilder()
                .withInterval(RandomUtils.randomPositiveInt())
                .withMethod(mockTask)
                .build();

        // when
        schedulerContainer.register(task1);
        schedulerContainer.register(task2);
        schedulerContainer.register(task3);

        // then
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(3, schedulers.size());
    }

    @Test
    void testPauseStopsExecutionsButKeepsTasksRegistered() throws Exception {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withInitialDelay(10)
                .withInitialDelayUnit(TimeUnit.MILLISECONDS)
                .withInterval(10)
                .withIntervalUnit(TimeUnit.MILLISECONDS)
                .withMethod(mockTask)
                .build();
        schedulerContainer.register(properties);
        verify(mockTask, timeout(1000).atLeastOnce()).run();

        // when
        schedulerContainer.pause();
        Thread.sleep(100); // let any tick in flight when pause() ran finish
        int invocationsAtPause = mockingDetails(mockTask).getInvocations().size();
        Thread.sleep(200);

        // then
        assertEquals(invocationsAtPause, mockingDetails(mockTask).getInvocations().size());
        List<?> schedulers = (List<?>) ReflectionUtils.getField(schedulerContainer, "schedulers");
        assertEquals(1, schedulers.size());
    }

    @Test
    void testResumeRestartsExecutions() throws Exception {
        // given
        SchedulingTaskProperties properties = new SchedulingTaskPropertiesBuilder()
                .withInitialDelay(10)
                .withInitialDelayUnit(TimeUnit.MILLISECONDS)
                .withInterval(10)
                .withIntervalUnit(TimeUnit.MILLISECONDS)
                .withMethod(mockTask)
                .build();
        schedulerContainer.register(properties);
        verify(mockTask, timeout(1000).atLeastOnce()).run();
        schedulerContainer.pause();
        Thread.sleep(100);
        int invocationsAtPause = mockingDetails(mockTask).getInvocations().size();

        // when
        schedulerContainer.resume();

        // then
        verify(mockTask, timeout(1000).atLeast(invocationsAtPause + 1)).run();
    }
}
