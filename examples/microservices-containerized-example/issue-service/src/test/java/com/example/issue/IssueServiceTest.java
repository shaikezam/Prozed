package com.example.issue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class IssueServiceTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();
    private final Gson gson = new Gson();

    @Test
    void testCreateIssueGeneratesKey() throws Exception {
        // PROZ has 5 seeded issues, so the next key is PROZ-6.
        String body = gson.toJson(new IssueController.IssueRequest(
            "PROZ", "TASK", "Add rate limiting", "Throttle login attempts.", "PROZ-1", "me", "HIGH"));

        DeserializedResponse<String> response = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/issues/")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            String.class);

        assertEquals(200, response.statusCode());
        assertEquals("PROZ-6", response.body());

        IssueController.Issue created = fetchIssue("PROZ-6");
        assertEquals("Add rate limiting", created.summary());
        assertEquals("TODO", created.status());
    }

    @Test
    void testTransitionUpdatesStatus() throws Exception {
        String body = gson.toJson(new IssueController.TransitionRequest("PROZ-2", "DONE"));

        DeserializedResponse<String> response = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/issues/transition")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            String.class);

        assertEquals(200, response.statusCode());
        assertEquals("DONE", fetchIssue("PROZ-2").status());
    }

    private IssueController.Issue fetchIssue(String issueKey) throws Exception {
        DeserializedResponse<IssueController.Issue[]> list = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/issues/").GET().build(), IssueController.Issue[].class);
        return Arrays.stream(list.body())
            .filter(i -> issueKey.equals(i.issueKey()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Issue not found: " + issueKey));
    }
}
