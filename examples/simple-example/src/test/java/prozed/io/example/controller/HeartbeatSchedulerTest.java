package prozed.io.example.controller;

import org.junit.jupiter.api.Test;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ProzedTest(mainClass = prozed.io.example.Main.class, cleanUp = true)
class HeartbeatSchedulerTest {

    private final HttpClientOperations httpClient = HttpClientOperations.createDefault();

    @Test
    void schedulerRunsAtLeastOnce() throws Exception {
        // when
        Thread.sleep(300); // > one 200ms tick interval

        // then
        assertTrue(fetchTicks() >= 1);
    }

    private int fetchTicks() throws Exception {
        HttpRequest request = httpClient.request("/heartbeat/ticks").GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return Integer.parseInt(response.body());
    }
}
