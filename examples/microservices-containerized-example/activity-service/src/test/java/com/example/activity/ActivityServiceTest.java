package com.example.activity;

import org.junit.jupiter.api.Test;
import prozed.io.core.api.web.ProzedServer;
import prozed.io.jms.JmsOperations;
import prozed.io.jms.api.DestinationType;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ActivityServiceTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();

    @Test
    void testActivityRecordedAndFetched() throws Exception {
        JmsOperations jms = (JmsOperations) ProzedServer.getContainer().get(JmsOperations.class);

        jms.sendMessage(
            new ActivityListener.IssueEvent("APP-1", "APP", "EPIC", "Authentication overhaul", "CREATED"),
            "issues.queue", DestinationType.QUEUE, Map.of("eventType", "CREATED"));

        ActivityController.ActivityRecord record = awaitRecord("APP-1");

        // the trailing [CREATED] proves the JMS property survived the broker round trip —
        // ActivityListener reads it back with message.getStringProperty("eventType")
        assertEquals("CREATED EPIC: Authentication overhaul [CREATED]", record.detail());
    }

    // The listener consumes off the queue on another thread, so poll the HTTP endpoint until our record lands.
    private ActivityController.ActivityRecord awaitRecord(String issueKey) throws Exception {
        for (int attempt = 0; attempt < 50; attempt++) {
            DeserializedResponse<ActivityController.ActivityRecord[]> response =
                httpClient.sendAndDeserializeWithResponse(
                    httpClient.request("/activity/history").GET().build(),
                    ActivityController.ActivityRecord[].class);
            assertEquals(200, response.statusCode());

            Optional<ActivityController.ActivityRecord> match = Arrays.stream(response.body())
                .filter(r -> issueKey.equals(r.issueKey()))
                .findFirst();
            if (match.isPresent()) {
                return match.get();
            }
            Thread.sleep(100);
        }
        fail("Activity was not recorded within timeout for " + issueKey);
        return null;
    }
}
