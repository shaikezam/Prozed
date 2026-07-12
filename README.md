[![Maven Central](https://img.shields.io/maven-central/v/io.github.shaikezam/prozed-core?label=Maven%20Central&color=blue)](https://central.sonatype.com/artifact/io.github.shaikezam/prozed-core)
![Java 21](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)
![CI](https://github.com/shaikezam/Prozed/workflows/CI/badge.svg)

# Prozed

**A tiny, zero-magic Java web framework.** Annotation-based REST routing, a real dependency-injection container, JDBC + JMS modules, and first-class integration testing — all on embedded Tomcat, in a few hundred KB. No reflection-heavy startup cost, no XML, no app server.

Prozed is for people who want to understand every layer of their stack: it reads like Spring, but you can read all of it in an afternoon.

```java
@Bean
@Controller(path = "/")
public class HelloController {

    @GetRequest(value = "/hello/{name}", produces = ContentType.TEXT_PLAIN)
    public String hello(@PathParam("{name}") String name) {
        return "Hello, " + name + "!";
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = new ProzedServer()) {
            server.start(); // GET http://localhost:8080/hello/world -> "Hello, world!"
        }
    }
}
```

---

## Table of Contents

- [Why Prozed?](#why-prozed)
- [Features](#features)
- [Requirements](#requirements)
- [Install](#install)
- [Quick Start](#quick-start)
- [Controllers & Routing](#controllers--routing)
- [Parameter Binding](#parameter-binding)
- [Dependency Injection](#dependency-injection)
- [Bean Lifecycle Hooks](#bean-lifecycle-hooks)
- [Servlet Filters](#servlet-filters)
- [Scheduled Tasks](#scheduled-tasks)
- [Database (prozed-jdbc)](#database-prozed-jdbc)
- [Messaging (prozed-jms)](#messaging-prozed-jms)
- [Testing (prozed-test)](#testing-prozed-test)
- [Configuration Reference](#configuration-reference)
- [Packaging & Deployment](#packaging--deployment)
- [Modules](#modules)
- [Building from Source](#building-from-source)
- [Limitations](#limitations)
- [Contributing](#contributing)
- [License](#license)

---

## Why Prozed?

| | Prozed |
| --- | --- |
| **Startup** | Embedded Tomcat, classpath scan — boots in well under a second |
| **Footprint** | Core has a handful of dependencies (Tomcat, Gson, SLF4J) |
| **Learning curve** | The whole framework is a small, readable codebase — no hidden proxies |
| **DI** | Field injection with compile-target-agnostic reflection, cycle detection |
| **Modular** | Pull in `prozed-jdbc` / `prozed-jms` only if you need them |
| **Testing** | Boot your real app in JUnit and hit it over HTTP, with optional auto-rollback |

If you've ever wanted a "Spring Boot, but I can actually read the source" — that's Prozed.

---

## Features

- 🚀 Embedded Tomcat HTTP server — no external container
- 🧭 Annotation-based routing on a radix tree (`@GetRequest`, `@PostRequest`, `@PutRequest`, `@DeleteRequest`)
- 🔌 Path, query, and JSON-body parameter binding with automatic type conversion
- 📦 Dependency-injection container with `@Bean` / `@Inject`, cycle detection, and lifecycle hooks
- 🧱 Servlet `Filter` support for cross-cutting concerns (auth, CORS, etc.)
- ⏰ Fixed-rate scheduled tasks on virtual threads, with per-task timeout and no-overlap guarantees
- 🗄️ Optional JDBC module: connection pooling, transactions, Flyway migrations
- 📨 Optional JMS module: send/consume messages with `@Listener` (ActiveMQ)
- 🧪 JUnit 5 integration testing that boots your real application, with optional per-test DB rollback
- 🔧 `${VAR}` / `${VAR:default}` environment-variable placeholders in `prozed.properties` — container-friendly config

---

## Requirements

- **Java 21+**
- **Maven 3.9+** (or Gradle)

---

## Install

Prozed is on **Maven Central** under `io.github.shaikezam` — no extra repository or credentials needed. Add the core module:

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

Add optional modules as needed:

```xml
<!-- JDBC: pooling, transactions, Flyway -->
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-jdbc</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- JMS: messaging with @Listener -->
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-jms</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Integration testing -->
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-test</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

> Use the latest version shown in the **Maven Central** badge at the top of this page.

---

## Quick Start

**1. Add `prozed.properties` to `src/main/resources`:**

```properties
web.service.port=8080
web.service.scan-package=com.example
```

**2. Write a controller:**

```java
package com.example;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.web.Controller;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.PathParam;

@Bean
@Controller(path = "/")
public class GreetingController {

    @GetRequest(value = "/greet/{name}")
    public Greeting greet(@PathParam("{name}") String name) {
        return new Greeting("Hello, " + name + "!");
    }

    public record Greeting(String message) {}
}
```

**3. Start the server:**

```java
package com.example;

import prozed.io.core.api.web.ProzedServer;

public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = new ProzedServer()) {
            server.start();
        }
    }
}
```

```bash
$ curl http://localhost:8080/greet/world
{"message":"Hello, world!"}
```

> `ProzedServer.start()` blocks (it runs the Tomcat accept loop), and `ProzedServer` implements `Closeable`, so a try-with-resources gives you a clean shutdown.

---

## Controllers & Routing

A controller is a `@Bean` annotated with `@Controller(path = "...")`. The full route of a handler is the controller `path` joined with the request annotation `value` (double slashes are collapsed):

```java
@Bean
@Controller(path = "/api/v1")
public class UserController {

    @Inject
    private UserService userService;

    @GetRequest(value = "/users/{id}")          // GET /api/v1/users/{id}
    public User getUser(@PathParam("{id}") int id) {
        return userService.findById(id)
                .orElseThrow(() -> new HttpException("User not found: " + id,
                        HttpServletResponse.SC_NOT_FOUND));
    }

    @PostRequest(value = "/users")              // POST /api/v1/users
    public int createUser(@PayloadParam User user) {
        return userService.create(user);
    }

    @PutRequest(value = "/users/{id}")          // PUT /api/v1/users/{id}
    public void updateUser(@PathParam("{id}") int id, @PayloadParam User user) {
        userService.update(user);
    }

    @DeleteRequest(value = "/users/{id}")       // DELETE /api/v1/users/{id}
    public void deleteUser(@PathParam("{id}") int id) {
        userService.delete(id);
    }

    @GetRequest(value = "/health", produces = ContentType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
```

### Responses

- Handlers return JSON by default (serialized with Gson).
- A `void` handler sends an empty `200 OK`.
- Set `produces = ContentType.TEXT_PLAIN` to return a string/number/boolean as plain text.
- Throw `HttpException(message, statusCode)` from anywhere in a handler to return a specific HTTP status with a JSON error body: `{"error": "..."}`.

### Error status codes

| Situation | Status |
| --- | --- |
| Path not registered | `404 Not Found` |
| Path exists but not for this HTTP method | `405 Method Not Allowed` |
| Unknown HTTP verb (e.g. `PATCH`) | `405 Method Not Allowed` |
| Malformed JSON body / missing required primitive param | `400 Bad Request` |
| `HttpException` thrown by your code | the code you pass |
| Any other uncaught exception | `500 Internal Server Error` |

---

## Parameter Binding

| Annotation | Binds | Example |
| --- | --- | --- |
| `@PathParam("{name}")` | A URL path segment | `/users/{id}` → `@PathParam("{id}") int id` |
| `@QueryParam("name")` | A query-string value | `?page=2` → `@QueryParam("page") int page` |
| `@PayloadParam` | The JSON request body | `@PayloadParam User user` (one per method) |
| `HttpServletRequest` | The raw servlet request | `HttpServletRequest request` (no annotation) |
| `HttpServletResponse` | The raw servlet response | `HttpServletResponse response` (no annotation) |

> ⚠️ **`@PathParam` value must include the braces** and exactly match the route placeholder. For the route `/users/{id}`, bind with `@PathParam("{id}")` — **not** `@PathParam("id")`.

### Type conversion & optionality

Path and query values are converted to the parameter's type automatically: `int`, `long`, `double`, `float`, `boolean`, `char`, their boxed equivalents, and `String`.

The **parameter type encodes whether the value is required**:

- **Primitive** (`int`, `long`, …): the value is **required**. A missing value returns `400 Bad Request`.
- **Boxed** (`Integer`, `Long`, …): the value is **optional**. A missing value is passed as `null` for you to handle.

```java
// page is required (400 if absent); size is optional (null if absent)
@GetRequest(value = "/users")
public List<User> list(@QueryParam("page") int page,
                       @QueryParam("size") Integer size) {
    int limit = size == null ? 20 : size;
    ...
}
```

### Query parameters — full example

```java
// GET /users/search?name=Bob&limit=10
@GetRequest(value = "/users/search")
public List<User> searchUsers(@QueryParam("name") String name,
                              @QueryParam("limit") Integer limit) {
    return userService.search(name, limit);   // limit is null when omitted
}
```

```bash
$ curl 'http://localhost:8080/users/search?name=Bob&limit=10'
[{"id":2,"name":"Bob"}]

$ curl 'http://localhost:8080/users/search?name=Bob'   # limit omitted -> defaulted in code
[{"id":2,"name":"Bob"}]
```

Query strings are split on `&`, keys/values are URL-decoded, and blank entries are ignored. A value is bound to the parameter by name and converted to its declared type.

### Raw servlet access — `HttpServletRequest` / `HttpServletResponse`

Need a header, the raw request, or fine-grained control over the response? Declare an `HttpServletRequest` and/or `HttpServletResponse` parameter — **no annotation required**. Prozed injects the live servlet objects. Mix them freely with `@PathParam` / `@QueryParam` / `@PayloadParam`.

```java
// Read a request header
@GetRequest(value = "/whoami")
public String whoami(HttpServletRequest request) {
    return request.getHeader("X-User");
}

// Write a response header + status directly
@GetRequest(value = "/download")
public void download(HttpServletResponse response) {
    response.setHeader("Content-Disposition", "attachment; filename=data.csv");
    response.setStatus(HttpServletResponse.SC_OK);
}

// Both, alongside a bound param
@GetRequest(value = "/users/{id}")
public User getUser(@PathParam("{id}") int id,
                    HttpServletRequest request,
                    HttpServletResponse response) {
    response.setHeader("X-Trace-Id", request.getHeader("X-Trace-Id"));
    return userService.findById(id).orElseThrow();
}
```

### Every handler parameter must be bound

Each parameter of a handler method **must** be one of: annotated with `@PathParam`, `@QueryParam`, or `@PayloadParam`, **or** typed as `HttpServletRequest` or `HttpServletResponse`. Prozed validates this at **startup** (when routes are registered) and fails fast with a clear message rather than throwing a `500` at request time:

```java
@GetRequest(value = "/bad")
public String bad(int page) { ... }   // ❌ startup error: "Unbound parameter 'page' in ... — add @PathParam, @QueryParam, @PayloadParam, HttpServletResponse or HttpServletRequest"

@GetRequest(value = "/ok")
public String ok(@QueryParam("page") int page) { ... }   // ✅

@GetRequest(value = "/raw")
public void raw(HttpServletResponse response) { ... }     // ✅ servlet params need no annotation

@GetRequest(value = "/health")
public String health() { ... }        // ✅ zero-parameter handlers are always valid
```

---

## Dependency Injection

Annotate classes with `@Bean` and inject collaborators into fields with `@Inject`:

```java
@Bean
public class UserService {

    @Inject
    private UserRepository userRepository;   // resolved from the container

    public Optional<User> findById(int id) {
        return Optional.ofNullable(userRepository.findById(id));
    }
}
```

- `@Bean` classes are discovered by scanning `web.service.scan-package` (and its sub-packages).
- Injection is **by field type** — the injected type must itself be a `@Bean`. Injecting a type that isn't a bean fails fast at startup with a clear message.
- The container **detects dependency cycles** at startup and reports the offending class instead of stack-overflowing.
- Module beans (from `prozed-jdbc` / `prozed-jms`) are registered automatically — see [Packaging & Deployment](#packaging--deployment) for the one fat-jar caveat.

---

## Bean Lifecycle Hooks

Add any of these **`public`, no-argument** methods to a bean and Prozed will call them at the right time. They are matched by name (no annotation needed); if absent, they're skipped.

> ⚠️ Hooks must be declared **directly on the bean class**. Methods inherited from a superclass or interface are **not** invoked, and a non-`public` or argument-taking method of the same name is silently ignored.

| Method | When |
| --- | --- |
| `preInit()` | After the bean's `@Inject` dependencies are wired |
| `postInit()` | After **all** beans are constructed and wired |
| `preDestroy()` | On server shutdown |
| `postDestroy()` | After `preDestroy`, during shutdown |

```java
@Bean
public class CacheWarmer {

    @Inject
    private UserRepository repository;

    public void postInit() {
        repository.warmCache();   // all beans are ready here
    }

    public void preDestroy() {
        // flush / release resources
    }
}
```

---

## Servlet Filters

Register standard Jakarta `Filter`s for cross-cutting concerns (auth, logging, CORS). Wrap each in a `FilterWrapper(name, urlPattern, filter)` and add it before `start()`:

```java
public class Main {
    public static void main(String[] args) {
        try (ProzedServer server = new ProzedServer()) {
            server.addFilter(new FilterWrapper("auth", "/admin/*", new AuthFilter()));
            server.addFilter(new FilterWrapper("cors", "/*", new CorsFilter()));
            server.start();
        }
    }
}
```

```java
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (request.getHeader("Authorization") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(req, res);
    }
}
```

---

## Scheduled Tasks

Prozed ships a small scheduler in **core** (no extra module). `SchedulerContainer` is registered as a bean automatically — `@Inject` it and `register(...)` your recurring jobs.

Unlike routing or lifecycle hooks, scheduling is **not** annotation-driven: there is no `@Scheduled`. You register tasks explicitly by handing the container a `SchedulingTaskProperties`. The natural place to do that is a bean's `postInit()` hook, once all beans are wired.

```java
@Bean
public class ReportScheduler {

    @Inject
    private SchedulerContainer scheduler;
    @Inject
    private ReportService reportService;

    public void postInit() {
        // first run after one interval (5 min), then every 5 minutes, no timeout
        scheduler.register(new SchedulingTaskProperties(
                "nightly-report",
                5, TimeUnit.MINUTES,
                reportService::generate));

        // wait 10s before the first run, then every 30s, interrupt a run if it exceeds 10s
        scheduler.register(new SchedulingTaskProperties(
                "sync-inventory",
                10, TimeUnit.SECONDS,     // initial delay
                30, TimeUnit.SECONDS,     // interval
                10, TimeUnit.SECONDS,     // timeout
                inventoryService::sync));
    }
}
```

### `SchedulingTaskProperties`

A record describing one job:

| Field | Meaning |
| --- | --- |
| `taskName` | Label used in logs |
| `initialDelay` + `initialDelayUnit` | Delay before the first run |
| `interval` + `intervalUnit` | Fixed-rate period between subsequent runs |
| `timeout` + `timeoutUnit` | Max run time; the execution is interrupted if it exceeds this. **`0` = no timeout** |
| `method` | The `Runnable` to execute |

Constructors, simplest to fullest:

```java
// first run after one interval; no timeout
new SchedulingTaskProperties(String taskName, long interval, TimeUnit intervalUnit, Runnable method);

// explicit initial delay; no timeout
new SchedulingTaskProperties(String taskName, long initialDelay, TimeUnit initialDelayUnit,
                             long interval, TimeUnit intervalUnit, Runnable method);

// explicit initial delay + timeout
new SchedulingTaskProperties(String taskName, long initialDelay, TimeUnit initialDelayUnit,
                             long interval, TimeUnit intervalUnit,
                             long timeout, TimeUnit timeoutUnit, Runnable method);
```

`initialDelay` and `interval` may use different `TimeUnit`s. Register a task with `scheduler.register(...)` and it starts on its own schedule; tasks are stopped for you on server shutdown.

### What to expect

- **Tasks don't overlap themselves** — if a run is still going when the next interval arrives, that run is skipped, not queued.
- **Timeout** — with `timeout > 0`, an overrunning run is interrupted. This stops work that responds to interruption (blocking I/O, JDBC query timeouts, code that checks `Thread.interrupted()`); a pure busy-loop that never checks won't be cut off.
- **Clean shutdown** — in-flight tasks are given a moment to finish (so open DB transactions can commit) before the server stops.
- **`interval` must be greater than 0.**

### Use cases

Cache warming/refresh, polling an external system, cleanup/retention jobs, periodic health or metric emission, outbox draining — anything you'd reach for `@Scheduled` in Spring.

---

## Database (prozed-jdbc)

Add `prozed-jdbc` and a JDBC driver (e.g. H2, PostgreSQL). `JdbcOperations` is registered as a bean automatically — just `@Inject` it.

```java
@Bean
public class UserRepository {

    @Inject
    private JdbcOperations jdbc;

    public User findById(int id) {
        return jdbc.selectOne(
                "SELECT id, name FROM users WHERE id = ?",
                rs -> new User(rs.getInt("id"), rs.getString("name")),
                id);
    }

    public List<User> findAll() {
        return jdbc.select(
                "SELECT id, name FROM users",
                rs -> new User(rs.getInt("id"), rs.getString("name")));
    }

    public int create(User user) {
        return jdbc.update("INSERT INTO users (name) VALUES (?)", user.name());
    }

    public void replaceAll(List<User> users) {
        jdbc.inTransaction(conn -> {           // atomic: all or nothing
            jdbc.update("DELETE FROM users");
            for (User u : users) {
                jdbc.update("INSERT INTO users (name) VALUES (?)", u.name());
            }
            return null;
        });
    }
}
```

### `JdbcOperations` API

| Method | Purpose |
| --- | --- |
| `select(sql, RowMapper<T>, params...)` | Query → `List<T>` |
| `selectOne(sql, RowMapper<T>, params...)` | Query → single `T` (or `null`) |
| `update(sql, params...)` | INSERT/UPDATE/DELETE → affected rows |
| `execute(sql, ResultSetHandler<T>, params...)` | Raw `ResultSet` access |
| `inTransaction(JdbcCallback<T>)` | Run work in a single transaction (commit on success, rollback on exception). Nested calls join the outer transaction. |

### Connection pooling

Backed by `tomcat-jdbc`. Every pool setting is tunable via `prozed.properties` — see the [Configuration Reference](#configuration-reference).

### Flyway migrations

Set `flyway.enabled=true` and drop SQL files in `src/main/resources/db/migration` (`V1__init.sql`, `V2__seed.sql`, …). Migrations run automatically at startup before your beans serve traffic.

```sql
-- src/main/resources/db/migration/V1__init.sql
CREATE TABLE users (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

---

## Messaging (prozed-jms)

Add `prozed-jms` (ships with the ActiveMQ client). `JmsOperations` is registered automatically.

### Sending

```java
@Bean
public class NotificationService {

    @Inject
    private JmsOperations jms;

    public void notifyUser(User user) {
        // object is serialized to JSON automatically
        jms.sendMessage(user, "user.events", DestinationType.QUEUE);
    }

    public void broadcast(String text) {
        jms.sendRawMessage(text, "announcements", DestinationType.TOPIC);
    }
}
```

`sendMessage(...)` accepts a single object or a `Collection<?>` and serializes it to JSON with Gson. `sendRawMessage(...)` sends a raw string. `DestinationType` is `QUEUE` or `TOPIC`.

### Consuming

A listener is a `@Bean` that implements `jakarta.jms.MessageListener` and is annotated with `@Listener`:

```java
@Bean
@Listener(destination = "user.events", destinationType = DestinationType.QUEUE)
public class UserEventListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventListener.class);

    @Override
    public void onMessage(Message message) {
        try {
            LOG.info("Received: {}", message.getBody(String.class));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
```

> A class annotated with `@Listener` **must** also be a `@Bean` and implement `MessageListener`; Prozed fails fast at startup with a clear message otherwise.

---

## Testing (prozed-test)

`@ProzedTest` boots your real application once per test class and lets you drive it over HTTP with `HttpClientOperations`.

```java
@ProzedTest(mainClass = com.example.Main.class)
class UserControllerTest {

    private final HttpClientOperations http = HttpClientOperations.createDefault();

    @Test
    void getsUser() throws Exception {
        var response = http.sendAndDeserializeWithResponse(
                http.request("/api/v1/users/1").GET().build(),
                User.class);

        assertEquals(200, response.statusCode());
        assertEquals("Alice", response.body().name());
    }

    @Test
    void createsUser() throws Exception {
        String body = new Gson().toJson(new User(0, "Bob"));
        var response = http.send(
                http.request("/api/v1/users")
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
```

`@ProzedTest` options:

| Attribute | Purpose |
| --- | --- |
| `mainClass` | Your application entry point (required) |
| `mainArgs` | Arguments passed to `main` |
| `cleanUp` | When `true` **and the `prozed-jdbc` test-jar is on the test classpath**, each test runs inside a transaction that is **rolled back** afterward, so tests don't pollute the database |

> The test port is read from the test classpath's `prozed.properties`.

### Accessing beans in a test

`@ProzedTest` boots the application but does **not** process `@Inject` on the test instance — annotating a test field does nothing and leaves it `null`. Drive the app over HTTP with `HttpClientOperations` for most cases. When a test needs a bean directly (for example, to publish a JMS message that has no HTTP entry point), pull it from the running container:

```java
JmsOperations jms = (JmsOperations) ProzedServer.getContainer().get(JmsOperations.class);
jms.sendMessage(event, "issues.queue", DestinationType.QUEUE);
```

`getContainer()` is available once the server has started (i.e. inside a `@Test` method), and `get(Class)` returns the same singleton the application uses — so writes still flow through `cleanUp`'s rolled-back connection.

### Enabling `cleanUp`

Database rollback is driven by `TestJdbcOperations`, which ships in the **`prozed-jdbc` test-jar**. The extension silently runs **without** rollback if that artifact is missing — every write commits and leaks into the next test. Add the test-jar (alongside `prozed-test`) to any module whose tests use `cleanUp = true`:

```xml
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-test</artifactId>
    <version>${prozed.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.shaikezam</groupId>
    <artifactId>prozed-jdbc</artifactId>
    <version>${prozed.version}</version>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>
```

`TestJdbcOperations` hands every query — including those run on Tomcat request threads during an HTTP call — a single shared, uncommitted connection, so `cleanUp` isolates HTTP-driven tests too.

---

## Configuration Reference

All settings live in `prozed.properties` on the classpath.

### Environment variable placeholders

Any value in `prozed.properties` may contain `${VAR}` or `${VAR:default}` placeholders. At startup Prozed replaces them:

1. **Environment variable** `VAR`, if set.
2. Otherwise the inline **`default`** (the part after `:`), if provided.
3. Otherwise an **empty string**.

Resolution is environment-only (JVM `-D` system properties are not consulted) and runs once, non-recursively, when properties are loaded.

```properties
# ${DB_HOST} has no default — env var is required, else resolves to ""
db.url=jdbc:mariadb://${DB_HOST}:${DB_PORT:3306}/app_db
db.username=${DB_USER:root}
db.password=${DB_PASSWORD}
```

This keeps secrets and per-environment values out of the packaged jar — ideal for containers, where Docker/Compose feed the variables:

```yaml
# docker-compose.yml
issue-service:
  image: eclipse-temurin:21-jre-alpine
  env_file:
    - .env          # DB_HOST, DB_PASSWORD, ... injected into the container env
  command: ["java", "-jar", "/app.jar"]
```

```dotenv
# .env
DB_HOST=task-tracker-mariadb
DB_PASSWORD=root
# DB_PORT / DB_USER omitted — the ${...:default} in prozed.properties applies
```

> A placeholder with no matching env var **and** no inline default resolves to an empty string, not an error — a value marked **Required** below will then fail later at the point it is used. Provide a default or ensure the variable is set.

See the [containerized microservices example](examples/microservices-containerized-example) for a full working stack.

### Core (`prozed-core`)

| Property | Description | Default |
| --- | --- | --- |
| `web.service.port` | Embedded Tomcat port | `8080` |
| `web.service.scan-package` | Base package scanned for `@Bean` / `@Controller` | **Required** |

### JDBC (`prozed-jdbc`)

| Property | Description | Default |
| --- | --- | --- |
| `db.url` | JDBC URL | **Required** |
| `db.driver-class-name` | JDBC driver class | **Required** |
| `db.username` | DB user | **Required** |
| `db.password` | DB password | **Required** |
| `db.pool.initial-size` | Initial pool size | `2` |
| `db.pool.min-idle` | Minimum idle connections | `2` |
| `db.pool.max-idle` | Maximum idle connections | `5` |
| `db.pool.max-active` | Maximum active connections | `20` |
| `db.pool.max-wait` | Max wait for a connection (ms) | `10000` |
| `db.pool.test-on-borrow` | Validate connection on borrow | `true` |
| `db.pool.validation-query` | Validation query | `SELECT 1` |
| `db.pool.validation-interval` | Validation interval (ms) | `30000` |
| `db.pool.remove-abandoned` | Reclaim abandoned connections | `true` |
| `db.pool.remove-abandoned-timeout` | Abandoned timeout (s) | `60` |
| `db.pool.log-abandoned` | Log abandoned connections | `true` |
| `flyway.enabled` | Run Flyway migrations at startup | `false` |
| `flyway.locations` | Migration scripts location | `classpath:db/migration` |
| `flyway.baseline-on-migrate` | Baseline an existing schema | `true` |
| `flyway.table` | Flyway history table | `flyway_schema_history` |

### JMS (`prozed-jms`)

| Property | Description | Default |
| --- | --- | --- |
| `jms.broker.type` | Broker type (e.g. `activemq`) | — |
| `jms.broker.url` | Broker URL (e.g. `tcp://localhost:61616`) | — |
| `jms.username` | Broker username | — |
| `jms.password` | Broker password | — |
| `jms.pool.max-connections` | Max pooled connections | `10` |
| `jms.pool.max-sessions-per-connection` | Max active sessions per connection | `500` |
| `jms.pool.idle-timeout` | Idle connection timeout (ms) | `30000` |

### Example: full `prozed.properties`

```properties
# Web
web.service.port=8080
web.service.scan-package=com.example

# Database (H2 in-memory)
db.url=jdbc:h2:mem:app;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
db.driver-class-name=org.h2.Driver
db.username=sa
db.password=
flyway.enabled=true

# JMS (ActiveMQ)
jms.broker.type=activemq
jms.broker.url=tcp://localhost:61616
jms.username=admin
jms.password=admin
jms.pool.max-connections=10
jms.pool.max-sessions-per-connection=500
jms.pool.idle-timeout=30000
```

---

## Packaging & Deployment

Prozed scans the classpath at startup, so **how you package matters**.

### ✅ Supported: thin jar + classpath, or a flat (shaded) uber-jar

Use the **Maven Shade plugin** or **Gradle Shadow** to produce a single flat jar where all classes live at the root (`app.jar!/com/example/...`).

### ⚠️ Critical: merge the service files when shading

Each optional module ships a service-registration file at the **same path**:
`META-INF/services/prozed.io.core.api.di.Bean`. When you shade multiple modules into one jar, the build will **overwrite** these files unless you tell it to **merge** them — and if it does, `JdbcOperations` / `JmsRegistry` silently disappear and you'll get startup failures that don't reproduce in your IDE.

**Maven Shade — full runnable-jar config:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.6.2</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
            <configuration>
                <!-- don't emit dependency-reduced-pom.xml -->
                <createDependencyReducedPom>false</createDependencyReducedPom>
                <transformers>
                    <!-- sets Main-Class so `java -jar` works -->
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.example.Main</mainClass>
                    </transformer>
                    <!-- merges META-INF/services/* instead of overwriting -->
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                </transformers>
                <filters>
                    <filter>
                        <artifact>*:*</artifact>
                        <excludes>
                            <!-- JPMS descriptors are meaningless in a flat uber-jar -->
                            <exclude>module-info.class</exclude>
                            <exclude>META-INF/versions/*/module-info.class</exclude>
                            <!-- drop signed-jar signatures, else SecurityException at startup -->
                            <exclude>META-INF/*.SF</exclude>
                            <exclude>META-INF/*.DSA</exclude>
                            <exclude>META-INF/*.RSA</exclude>
                        </excludes>
                    </filter>
                </filters>
            </configuration>
        </execution>
    </executions>
</plugin>
```

> Without the `ManifestResourceTransformer`, the jar has no `Main-Class` and `java -jar` fails with *"no main manifest attribute"*. The `filters` silence the common `module-info.class` shading warning and prevent a signed transitive dependency (e.g. via ActiveMQ) from triggering `SecurityException: Invalid signature file digest` at startup. Overlapping `META-INF/LICENSE`/`NOTICE` warnings are cosmetic — exclude them too if you want a clean build log.

**Gradle Shadow:**

```groovy
shadowJar {
    mergeServiceFiles()   // does the same thing
}
```

### ❌ Not supported: Spring Boot–style nested fat jars

The `spring-boot-maven-plugin` `repackage` goal produces a **nested** layout (`BOOT-INF/classes`, `BOOT-INF/lib/*.jar`) that requires Spring Boot's custom class loader. Prozed's scanner does not read nested jars — package with Shade/Shadow instead.

> Note: classpath scanning relies on the jar containing **directory entries** for your packages (standard Maven jars include them).

---

## Modules

| Module | Description |
| --- | --- |
| `prozed-core` | DI container, routing, embedded Tomcat server, configuration, classpath scanning, scheduled tasks |
| `prozed-jdbc` | `JdbcOperations`, connection pooling, transactions, Flyway migrations |
| `prozed-jms` | `JmsOperations`, `@Listener` consumers (ActiveMQ) |
| `prozed-test` | `@ProzedTest` JUnit 5 extension + `HttpClientOperations` |

Runnable sample apps live under [`examples/`](examples):

| Example | Description |
| --- | --- |
| [`simple-example`](examples/simple-example) | A single-process app exercising every module (REST + JDBC + JMS + filters + tests) |
| [`microservices-containerized-example`](examples/microservices-containerized-example) | A task tracker as three Prozed microservices + PHP UI, wired with JMS and Docker Compose |

The fastest way to learn Prozed is to read [`simple-example`](examples/simple-example) end to end.

---

## Building from Source

```bash
git clone https://github.com/shaikezam/Prozed.git
cd Prozed
mvn clean install
```

Run the [`simple-example`](examples/simple-example) app as a self-contained runnable jar:

```bash
# build the example (and the modules it depends on) into one fat jar
mvn -pl examples/simple-example -am clean package

# run it — no Maven needed at runtime
java -jar examples/simple-example/target/simple-example-1.0.0-SNAPSHOT.jar
```

`simple-example` configures the Shade plugin (see [Packaging & Deployment](#packaging--deployment)), so `package` emits a runnable uber-jar with the module service files merged. For a multi-service, containerized setup, see [`microservices-containerized-example`](examples/microservices-containerized-example).

Requires JDK 21+ and Maven 3.9+.

---

## Limitations

Prozed is deliberately small. Current constraints worth knowing before you adopt it:

- **One `ProzedServer` per JVM** — the DI container and port are process-global.
- **Field injection only** (no constructor or setter injection), and injection is by concrete bean type (no interface-to-implementation binding).
- **Flat / thin-jar packaging only** — see [Packaging & Deployment](#packaging--deployment).
- JMS support targets **ActiveMQ**.

Found a rough edge? [Open an issue](https://github.com/shaikezam/Prozed/issues) — see below.

---

## Contributing

Contributions are very welcome — this is a small, approachable codebase, which makes it a great place to land your first framework PR.

1. Fork the repo and create a branch: `git checkout -b feature/my-change`
2. Make your change and add/adjust tests
3. Run `mvn clean install` and make sure everything is green
4. Open a Pull Request describing the change and the why

Bug reports and feature ideas are equally welcome via [GitHub Issues](https://github.com/shaikezam/Prozed/issues). If you find Prozed useful, a ⭐ helps others discover it.

---

## License

[MIT](LICENSE) © Shai Zambrovski
