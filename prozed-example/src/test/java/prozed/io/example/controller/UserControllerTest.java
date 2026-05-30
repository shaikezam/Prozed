package prozed.io.example.controller;

import org.junit.jupiter.api.Test;
import prozed.io.example.model.User;
import prozed.io.example.model.UserBuilder;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = prozed.io.example.Main.class)
public class UserControllerTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault(8081, "/");

    @Test
    void testGetUser() throws Exception {
        HttpRequest request = httpClient.request("/user/1")
                .GET()
                .build();
        User excepted = new UserBuilder()
                .withId(1)
                .withName("Alice")
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
}
