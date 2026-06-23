package com.example.activity;

import prozed.io.core.api.web.ProzedServer;

public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = new ProzedServer()) {
            server.start();
        }
    }
}
