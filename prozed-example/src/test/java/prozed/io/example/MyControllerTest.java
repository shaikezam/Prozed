package prozed.io.example;

import org.junit.jupiter.api.Test;
import prozed.io.example.controller.TempController;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;
import prozed.io.test.operations.HttpClientOperations.DeserializedResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = prozed.io.example.Main.class)
public class MyControllerTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault(8082, "/temp");

    @Test
    void testHello() throws Exception {
        HttpRequest request = httpClient.request("/hello")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("Hello from TempController!", response.body());
    }

    @Test
    void testNumber() throws Exception {
        HttpRequest request = httpClient.request("/number")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("42", response.body());
    }

    @Test
    void testEnabled() throws Exception {
        HttpRequest request = httpClient.request("/enabled")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("true", response.body());
    }

    @Test
    void testStatus() throws Exception {
        HttpRequest request = httpClient.request("/status?hi=test&hi2=123")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("{\"status\": \"ok\", \"controller\": \"TempController\"}", response.body());
    }

    @Test
    void testGetUser() throws Exception {
        HttpRequest request = httpClient.request("/user")
                .GET()
                .build();

        DeserializedResponse<TempController.User> response = httpClient.sendAndDeserializeWithResponse(request, TempController.User.class);

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertEquals(1, response.body().id());
        assertEquals("shaikezam", response.body().name());
    }

    @Test
    void testEcho() throws Exception {
        TempController.User user = new TempController.User(99, "testUser");
        String jsonBody = new com.google.gson.Gson().toJson(user);

        HttpRequest request = httpClient.request("/echo")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertEquals("testUser", response.body());
    }
}
