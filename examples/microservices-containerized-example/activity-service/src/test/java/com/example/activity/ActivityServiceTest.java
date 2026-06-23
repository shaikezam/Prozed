package com.example.activity;

import org.junit.jupiter.api.Test;
import prozed.io.core.api.di.Inject;
import prozed.io.jms.JmsOperations;
import prozed.io.jms.api.DestinationType;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ActivityServiceTest {

    @Inject
    private JmsOperations jms;

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();

    @Test
    void testActivityRecordedAndFetched() throws Exception {
        // issue-service publishes IssueEvents to this queue; the @Listener records them.
        jms.sendMessage(
            new ActivityListener.IssueEvent("PROZ-1", "PROZ", "EPIC", "Authentication overhaul", "CREATED"),
            "issues.queue", DestinationType.QUEUE);

        ActivityController.ActivityRecord[] history = awaitHistory();

        assertEquals(1, history.length);
        assertEquals("PROZ-1", history[0].issueKey());
        assertEquals("CREATED EPIC: Authentication overhaul", history[0].detail());
    }

    // The listener consumes off the queue on another thread, so poll the HTTP endpoint until it lands.
    private ActivityController.ActivityRecord[] awaitHistory() throws Exception {
        for (int attempt = 0; attempt < 50; attempt++) {
            DeserializedResponse<ActivityController.ActivityRecord[]> response =
                httpClient.sendAndDeserializeWithResponse(
                    httpClient.request("/activity/history").GET().build(),
                    ActivityController.ActivityRecord[].class);
            assertEquals(200, response.statusCode());
            if (response.body().length > 0) {
                return response.body();
            }
            Thread.sleep(100);
        }
        fail("Activity was not recorded within timeout");
        return new ActivityController.ActivityRecord[0];
    }
}
