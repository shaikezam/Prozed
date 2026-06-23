package com.example.project;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ProjectServiceTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();
    private final Gson gson = new Gson();

    @Test
    void testCreateProject() throws Exception {
        String body = gson.toJson(
            new ProjectController.ProjectRequest("APP", "Mobile App", "iOS and Android app."));

        DeserializedResponse<String> response = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/projects/")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            String.class);

        assertEquals(200, response.statusCode());
        assertEquals("Project APP created.", response.body());
    }

    @Test
    void testListReturnsCreatedProject() throws Exception {
        String body = gson.toJson(
            new ProjectController.ProjectRequest("APP", "Mobile App", "iOS and Android app."));
        httpClient.send(
            httpClient.request("/projects/")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            java.net.http.HttpResponse.BodyHandlers.ofString());

        DeserializedResponse<ProjectController.Project[]> list = httpClient.sendAndDeserializeWithResponse(
            httpClient.request("/projects/").GET().build(), ProjectController.Project[].class);

        assertEquals(200, list.statusCode());
        assertTrue(Arrays.stream(list.body()).anyMatch(p -> "APP".equals(p.projectKey())));
    }
}
