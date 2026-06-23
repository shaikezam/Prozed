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
    void testCreateIssueGeneratesSequentialKey() throws Exception {
        // No issues are seeded under the APP project, so the first created issue is APP-1.
        DeserializedResponse<String> response = createIssue(
            "APP", "TASK", "Add rate limiting", "Throttle login attempts.", null, "me", "HIGH");

        assertEquals(200, response.statusCode());
        assertEquals("APP-1", response.body());

        IssueController.Issue created = fetchIssue("APP-1");
        assertEquals("Add rate limiting", created.summary());
        assertEquals("TODO", created.status());
    }

    @Test
    void testTransitionUpdatesStatus() throws Exception {
        // Create the issue this test owns, then transition it — no reliance on seeded rows.
        String issueKey = createIssue(
            "APP", "TASK", "Add rate limiting", "Throttle login attempts.", null, "me", "HIGH").body();
        assertEquals("TODO", fetchIssue(issueKey).status());

        String body = gson.toJson(new IssueController.TransitionRequest(issueKey, "DONE"));
        DeserializedResponse<String> response = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/issues/transition")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            String.class);

        assertEquals(200, response.statusCode());
        assertEquals("DONE", fetchIssue(issueKey).status());
    }

    private DeserializedResponse<String> createIssue(String projectKey, String type, String summary,
                                                     String description, String parentKey,
                                                     String assignee, String priority) throws Exception {
        String body = gson.toJson(new IssueController.IssueRequest(
            projectKey, type, summary, description, parentKey, assignee, priority));
        return httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/issues/")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            String.class);
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
