![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)
![CI](https://github.com/shaikezam/Prozed/workflows/CI/badge.svg)

# Prozed Framework

A lightweight Java web framework with built-in dependency injection and REST API support.

## Features

- Lightweight embedded Tomcat server
- Annotation-based routing
- Path, query, and JSON payload binding
- JSON and plain text responses
- Dependency injection container
- JUnit integration for HTTP-level tests

## Quick Start

Add a `prozed.properties` file to `src/main/resources`:

```properties
web.service.port=8080
web.service.scan-package=com.example
```

Start Prozed from your application entry point:

```java
package com.example;

import prozed.io.core.internal.ProzedServer;

public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = new ProzedServer()) {
            server.start();
        }
    }
}
```

## Controllers

Create REST endpoints with simple annotations:

```java
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.PostRequest;

@Bean
@Controller(path = "/")
public class UserController {

    @Inject
    private UserService userService;

    @GetRequest(value = "/user/{id}")
    public User getUser(@PathParam("{id}") int id) {
        return userService.getUser(id).orElseThrow();
    }

    @PostRequest(value = "/user")
    public int createUser(@PayloadParam User user) {
        return userService.createUser(user);
    }

    @GetRequest(value = "/health", produces = ContentType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
```

Routes are built from the controller `path` plus the request annotation `value`. By default, handlers produce JSON. Set `produces = ContentType.TEXT_PLAIN` when returning plain strings, numbers, or booleans as text.

## Services

Create business logic services with `@Bean`:

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import prozed.io.example.model.User;

import prozed.io.core.api.di.Bean;

@Bean
public class UserService {

    private final Map<Integer, User> userRepository = new HashMap<>();

    public UserService() {
        userRepository.put(1, new User(1, "a"));
    }

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(userRepository.get(id));
    }

    public int createUser(User user) {
        userRepository.putIfAbsent(user.id(), user);
        return user.id();
    }
}
```

`@Bean` classes are discovered from `web.service.scan-package`. Fields marked with `@Inject` are resolved from the Prozed dependency injection container.

## Configuration

Prozed reads `prozed.properties` from the application classpath.

| Property | Description | Default |
| --- | --- | --- |
| `web.service.port` | Embedded Tomcat port | `8080` |
| `web.service.scan-package` | Base package scanned for `@Bean` and `@Controller` classes | Required |

## Annotations

### Web
- `@Controller(path = "/path")` - Mark a class as a controller and set its base route
- `@GetRequest(value = "/path")` - Handle GET requests
- `@PostRequest(value = "/path")` - Handle POST requests
- `@PutRequest(value = "/path")` - Handle PUT requests
- `@DeleteRequest(value = "/path")` - Handle DELETE requests
- `produces = ContentType.APPLICATION_JSON` - Default JSON response content type
- `produces = ContentType.TEXT_PLAIN` - Plain text response content type

### Parameters
- `@PathParam("name")` - Bind URL path parameters such as `/{id}`
- `@QueryParam("name")` - Bind query string parameters such as `?page=1`
- `@PayloadParam` - Bind the JSON request body to an object

### Dependency Injection
- `@Bean` - Mark a class for the DI container
- `@Inject` - Inject another bean into a field


## Testing

Use `prozed-test` to start your application before JUnit tests run:

```java
import org.junit.jupiter.api.Test;
import prozed.io.example.model.User;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.operations.HttpClientOperations;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ProzedTest(mainClass = com.example.Main.class)
class UserControllerTest {

    private final HttpClientOperations http = HttpClientOperations.createDefault(8080, "/");

    @Test
    void getsUser() throws Exception {
        HttpRequest request = http.request("/user/1")
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void createsUser() throws Exception {
        User user = new User(2, "b");
        String jsonBody = new com.google.gson.Gson().toJson(user);

        HttpRequest request = http.request("/user")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("2", response.body());
    }
}
```

## Maven

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

For tests, add:

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-test</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## License

MIT License


