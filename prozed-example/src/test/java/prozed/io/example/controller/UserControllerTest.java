package prozed.io.example.controller;

import org.junit.jupiter.api.Test;
import prozed.io.example.model.User;
import prozed.io.example.model.UserBuilder;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = prozed.io.example.Main.class)
public class UserControllerTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault(8081, "/");

    @Test
    void testGetUser() throws Exception {
        HttpRequest request = httpClient.request("/user/2")
                .GET()
                .build();
        User excepted = new UserBuilder()
                .withId(2)
                .withName("Bob")
                .build();

        DeserializedResponse<User> response = httpClient.sendAndDeserializeWithResponse(request, User.class);

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertEquals(excepted, response.body());
    }

    @Test
    void testHealth() throws Exception {
        HttpRequest request = httpClient.request("/health")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("OK", response.body());
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User(2, "b");
        String jsonBody = new com.google.gson.Gson().toJson(user);

        HttpRequest request = httpClient.request("/user")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertEquals("1", response.body());
    }

    @Test
    void testCreateUsers() throws Exception {
        List<User> users = List.of(
                new UserBuilder()
                        .build(),
                new UserBuilder()
                        .build());
        String jsonBody = new com.google.gson.Gson().toJson(users);

        HttpRequest request = httpClient.request("/users")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testUpdateUser() throws Exception {
        // given
        User updated = new UserBuilder()
                .withId(1)
                .withName("UpdatedAlice")
                .build();
        String jsonBody = new com.google.gson.Gson().toJson(updated);

        // when
        HttpResponse<String> updateResponse = httpClient.send(
                httpClient.request("/user/1")
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        DeserializedResponse<User> getResponse = httpClient.sendAndDeserializeWithResponse(
                httpClient.request("/user/1").GET().build(), User.class
        );

        // then
        assertEquals(200, updateResponse.statusCode());
        assertEquals("UpdatedAlice", getResponse.body().name());
    }

    @Test
    void testDeleteUser() throws Exception {
        // when
        HttpResponse<String> deleteResponse = httpClient.send(
                httpClient.request("/user/1")
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpResponse<String> getResponse = httpClient.send(
                httpClient.request("/user/1").GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // then
        assertEquals(200, deleteResponse.statusCode());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void testProtectFilter() throws Exception {
        // given
        HttpRequest request = httpClient.request("/protect")
                .GET()
                .build();

        // when
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(401, response.statusCode());
    }

    @Test
    void testMethodNotAllowedFilter() throws Exception {
        // given
        HttpRequest request = httpClient.request("/api/v1/test")
                .GET()
                .build();

        // when
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(405, response.statusCode());
    }
}
