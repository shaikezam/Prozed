package prozed.io.test;

import prozed.io.core.internal.ProzedServer;

public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = ProzedServer.builder()
                .withPort(8080)
                .withContextPath("/")
                .scan("prozed.io.example")
                .build()) {
            server.start();
        }
    }
}
