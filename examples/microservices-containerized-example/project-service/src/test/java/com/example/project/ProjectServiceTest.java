package com.example.project;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ProjectServiceTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();
    private final Gson gson = new Gson();

    @Test
    void testCreateProjectReturnsConfirmation() throws Exception {
        HttpResponse<String> response = createProject("APP", "Mobile App", "iOS and Android app.");

        assertEquals(200, response.statusCode());
        assertEquals("Project APP created.", response.body());
    }

    @Test
    void testCreatedProjectAppearsInList() throws Exception {
        createProject("APP", "Mobile App", "iOS and Android app.");

        DeserializedResponse<ProjectController.Project[]> list = httpClient
                .sendAndDeserializeWithResponse(
                        httpClient.request("/projects/")
                                .GET()
                                .build(), ProjectController.Project[].class);

        assertEquals(200, list.statusCode());
        assertTrue(Arrays.stream(list.body()).anyMatch(p -> "APP".equals(p.projectKey())));
    }

    private HttpResponse<String> createProject(String key, String name, String description) throws Exception {
        String body = gson.toJson(new ProjectController.ProjectRequest(key, name, description));
        return httpClient.sendAndDeserializeWithRawResponse(
                httpClient.request("/projects/")
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build());
    }
}
