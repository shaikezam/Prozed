package com.example.activity;

import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import prozed.io.core.api.di.Inject;
import prozed.io.test.api.ProzedTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ActivityServiceTest {

    @Inject
    private ActivityListener activityListener;

    @Inject
    private ActivityController activityController;

    @Test
    public void testActivityRecordedAndFetched() throws Exception {
        Message mockMessage = Mockito.mock(Message.class);
        when(mockMessage.getBody(String.class)).thenReturn(
            "{\"issueKey\":\"PROZ-1\",\"projectKey\":\"PROZ\",\"type\":\"EPIC\",\"summary\":\"Authentication overhaul\",\"action\":\"CREATED\"}");

        activityListener.onMessage(mockMessage);

        List<ActivityController.ActivityRecord> history = activityController.getHistory();
        assertEquals(1, history.size());
        assertEquals("PROZ-1", history.get(0).issueKey());
        assertEquals("CREATED EPIC: Authentication overhaul", history.get(0).detail());
    }
}
