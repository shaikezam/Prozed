# Prozed Framework

A lightweight, high-performance Java web framework built on embedded Tomcat with dependency injection and radix tree routing.

## Features

- **Lightweight**: Minimal overhead with embedded Tomcat
- **Dependency Injection**: Field-based DI with `@Inject` and `@Bean` annotations
- **Fast Routing**: Radix tree for O(k) route lookup performance
- **RESTful APIs**: Support for GET, POST, PUT, DELETE HTTP methods
- **Annotation-driven**: Clean, declarative API design
- **Auto-scanning**: Automatic discovery of controllers and beans

## Quick Start

### 1. Add Dependencies

Add Prozed to your Maven project:

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Create a Main Class

```java
package prozed.io.example;

import prozed.io.core.internal.ProzedServer;

public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = ProzedServer.builder()
                .withPort(8080)
                .withContextPath("/")
                .scan("prozed.io.example")  // Scan your package
                .build()) {
            server.start();
        }
    }
}
```

### 3. Create a Controller

```java
package prozed.io.example.controller;

import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PostRequest;
import prozed.io.core.api.web.Produces;

@Controller(path = "/api")
public class UserController {

    @GetRequest(path = "/users")
    @Produces("application/json")
    public String getUsers() {
        return "[{\"id\": 1, \"name\": \"John\"}]";
    }

    @GetRequest(path = "/users/{id}")
    @Produces("application/json")
    public String getUser(String id) {
        return "{\"id\": " + id + ", \"name\": \"John\"}";
    }

    @PostRequest(path = "/users")
    @Produces("application/json")
    public String createUser(String userData) {
        return "{\"id\": 2, \"status\": \"created\"}";
    }
}
```

## API Reference

### Web Annotations

#### `@Controller`
Marks a class as a web controller.

```java
@Controller(path = "/base-path")
public class MyController {
    // controller methods
}
```

#### HTTP Method Annotations
- `@GetRequest(path = "/path")` - Handle GET requests
- `@PostRequest(path = "/path")` - Handle POST requests  
- `@PutRequest(path = "/path")` - Handle PUT requests
- `@DeleteRequest(path = "/path")` - Handle DELETE requests

#### `@Produces`
Specifies the response content type.

```java
@Produces("application/json")
@Produces("text/plain")
@Produces("text/html")
```

#### `@Consumes`
Specifies the request content type.

```java
@Consumes("application/json")
@Consumes("application/x-www-form-urlencoded")
```

### Dependency Injection Annotations

#### `@Bean`
Marks a class for dependency injection.

```java
@Bean
public class UserService {
    public String getUser(String id) {
        return "User: " + id;
    }
}
```

#### `@Inject`
Injects dependencies into fields.

```java
@Controller(path = "/api")
public class UserController {
    
    @Inject
    private UserService userService;
    
    @GetRequest(path = "/users/{id}")
    public String getUser(String id) {
        return userService.getUser(id);
    }
}
```

## Dependency Injection

Prozed provides automatic dependency injection with the following features:

- **Field Injection**: Use `@Inject` on fields
- **Singleton Scope**: All beans are singletons by default
- **Circular Dependency Detection**: Automatically detects and prevents circular dependencies
- **Auto-wiring**: Dependencies are automatically resolved and injected

### Example

```java
@Bean
public class DatabaseService {
    public String query(String sql) {
        return "Query result: " + sql;
    }
}

@Bean
public class UserService {
    @Inject
    private DatabaseService databaseService;
    
    public String getUser(String id) {
        return databaseService.query("SELECT * FROM users WHERE id = " + id);
    }
}

@Controller(path = "/api")
public class UserController {
    @Inject
    private UserService userService;
    
    @GetRequest(path = "/users/{id}")
    @Produces("application/json")
    public String getUser(String id) {
        return userService.getUser(id);
    }
}
```

## Server Configuration

### Builder Pattern

Configure your server using the fluent builder API:

```java
ProzedServer server = ProzedServer.builder()
    .withPort(8080)                    // Server port (default: 8080)
    .withContextPath("/api")            // Context path (default: "")
    .scan("com.yourpackage")           // Package to scan for controllers and beans
    .build();
```

### Configuration Options

- **Port**: Server listening port
- **Context Path**: Base path for all routes
- **Scan Package**: Package to scan for `@Controller` and `@Bean` classes

## Routing

Prozed uses a radix tree for efficient routing with support for:

- Static paths: `/users`, `/api/status`
- Parameterized paths: `/users/{id}`, `/posts/{slug}`
- HTTP method routing: GET, POST, PUT, DELETE

### Route Examples

```java
@Controller(path = "/api/v1")
public class ApiController {
    
    // GET /api/v1/users
    @GetRequest(path = "/users")
    public String getUsers() { ... }
    
    // GET /api/v1/users/123
    @GetRequest(path = "/users/{id}")
    public String getUser(String id) { ... }
    
    // POST /api/v1/users
    @PostRequest(path = "/users")
    public String createUser(String userData) { ... }
    
    // PUT /api/v1/users/123
    @PutRequest(path = "/users/{id}")
    public String updateUser(String id, String userData) { ... }
    
    // DELETE /api/v1/users/123
    @DeleteRequest(path = "/users/{id}")
    public String deleteUser(String id) { ... }
}
```

## Error Handling

Prozed provides built-in error handling for common scenarios:

- **Missing Dependencies**: Throws `IllegalStateException` if `@Inject` targets are not marked as `@Bean`
- **Circular Dependencies**: Detects and throws `IllegalStateException` for circular references
- **Creation Failures**: Throws `RuntimeException` if bean instantiation fails
- **Route Conflicts**: Automatically handles route registration conflicts

## Best Practices

1. **Package Structure**: Organize your code with clear package separation
   ```
   com.yourapp/
   ├── controller/     # REST controllers
   ├── service/       # Business logic
   ├── repository/    # Data access
   └── model/         # Data models
   ```

2. **Dependency Injection**: Use `@Inject` for all dependencies, avoid manual instantiation

3. **RESTful Design**: Follow REST conventions for your API endpoints

4. **Error Handling**: Implement proper error responses and status codes

5. **Content Types**: Always specify `@Produces` and `@Consumes` for clarity

## Example Project Structure

```
src/main/java/
└── com/
    └── yourapp/
        ├── Main.java                    # Application entry point
        ├── controller/
        │   ├── UserController.java
        │   └── ProductController.java
        ├── service/
        │   ├── UserService.java
        │   └── ProductService.java
        └── repository/
            ├── UserRepository.java
            └── ProductRepository.java
```

## Running the Application

1. Build your project:
   ```bash
   mvn clean compile
   ```

2. Run the main class:
   ```bash
   mvn exec:java -Dexec.mainClass="com.yourapp.Main"
   ```

3. Access your API:
   ```
   http://localhost:8080/api/users
   http://localhost:8080/api/users/123
   ```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
