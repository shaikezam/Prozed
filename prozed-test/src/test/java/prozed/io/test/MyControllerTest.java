package prozed.io.test;

import org.junit.jupiter.api.Test;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.controller.TempController;
import prozed.io.test.operations.HttpClientOperations;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ProzedTest(mainClass = prozed.io.test.Main.class)
public class MyControllerTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();
    private final String baseUrl = "http://localhost:8080/temp";

    @Test
    void testHello() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hello"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Hello from TempController!", response.body());
    }

    @Test
    void testStatus() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/status?hi=test&hi2=123"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"status\": \"ok\", \"controller\": \"TempController\"}", response.body());
    }

    @Test
    void testGetUser() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/user"))
                .GET()
                .build();

        TempController.User user = httpClient.sendAndDeserialize(request, TempController.User.class);

        assertEquals(1, user.id());
        assertEquals("shaikezam", user.name());
    }

    @Test
    void testEcho() throws Exception {
        TempController.User user = new TempController.User(99, "testUser");
        String jsonBody = new com.google.gson.Gson().toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/echo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("testUser", response.body());
    }
}