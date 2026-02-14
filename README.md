![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)
![CI](https://github.com/shaikezam/Prozed/workflows/CI/badge.svg)

# Prozed Framework

A lightweight Java web framework with built-in dependency injection and REST API support.

## Quick Start

```java
ProzedServer.builder()
    .scan("com.example.controllers")
    .withPort(8080)
    .build()
    .start();
```

## Controllers

Create REST endpoints with simple annotations:

```java
@Bean
@Controller("/api/users")
public class UserController {
    
    @Inject
    private UserService userService;
    
    @GetRequest("/{id}")
    public User getUser(@PathParam("id") String id) {
        return userService.findById(id);
    }
    
    @PostRequest("/")
    public User createUser(@PayloadParam("user") User user) {
        return userService.create(user);
    }
    
    @GetRequest("/")
    public List<User> getUsers(@QueryParam("page") int page) {
        return userService.findAll(page);
    }
}
```

## Services

Create business logic services with @Bean:

```java
@Bean
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
    public User findById(String id) {
        return userRepository.findById(id);
    }
    
    public User create(User user) {
        return userRepository.save(user);
    }
    
    public List<User> findAll(int page) {
        return userRepository.findAll(page);
    }
}
```

## Annotations

### Web
- `@Controller("/path")` - Mark class as controller
- `@GetRequest("/path")` - Handle GET requests
- `@PostRequest("/path")` - Handle POST requests
- `@PutRequest("/path")` - Handle PUT requests
- `@DeleteRequest("/path")` - Handle DELETE requests

### Parameters
- `@PathParam("name")` - URL path parameters
- `@QueryParam("name")` - Query string parameters
- `@PayloadParam("name")` - Request body (JSON)

### Dependency Injection
- `@Bean` - Mark class for DI container (all beans are singletons)

## Features

- ✅ Lightweight embedded Tomcat server
- ✅ Annotation-based routing
- ✅ Path and query parameter binding
- ✅ JSON request/response handling
- ✅ Dependency injection container
- ✅ Type-safe parameter validation

## Maven

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## License

MIT License
