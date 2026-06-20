![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)
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
- 🗄️ Optional JDBC module: connection pooling, transactions, Flyway migrations
- 📨 Optional JMS module: send/consume messages with `@Listener` (ActiveMQ)
- 🧪 JUnit 5 integration testing that boots your real application, with optional per-test DB rollback

---

## Requirements

- **Java 17+**
- **Maven 3.9+** (or Gradle)

---

## Install

Prozed is published under `io.github.shaikezam`. Add the core module:

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

> Replace `1.0.0` with the latest released version.

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

### Every handler parameter must be bound

Each parameter of a handler method **must** carry exactly one of `@PathParam`, `@QueryParam`, or `@PayloadParam`. Prozed validates this at **startup** (when routes are registered) and fails fast with a clear message rather than throwing a `500` at request time:

```java
@GetRequest(value = "/bad")
public String bad(int page) { ... }   // ❌ startup error: "Unbound parameter 'page' in ... — add @PathParam, @QueryParam, or @PayloadParam"

@GetRequest(value = "/ok")
public String ok(@QueryParam("page") int page) { ... }   // ✅

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

    private final HttpClientOperations http = HttpClientOperations.createDefault(8080, "/");

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
| `cleanUp` | When `true` **and `prozed-jdbc` is on the classpath**, each test runs inside a transaction that is **rolled back** afterward, so tests don't pollute the database |

> The test port is read from the test classpath's `prozed.properties` — point `HttpClientOperations` at the same port.

---

## Configuration Reference

All settings live in `prozed.properties` on the classpath.

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

| Property | Description |
| --- | --- |
| `jms.broker.type` | Broker type (e.g. `activemq`) |
| `jms.broker.url` | Broker URL (e.g. `tcp://localhost:61616`) |
| `jms.username` | Broker username |
| `jms.password` | Broker password |

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
| `prozed-core` | DI container, routing, embedded Tomcat server, configuration, classpath scanning |
| `prozed-jdbc` | `JdbcOperations`, connection pooling, transactions, Flyway migrations |
| `prozed-jms` | `JmsOperations`, `@Listener` consumers (ActiveMQ) |
| `prozed-test` | `@ProzedTest` JUnit 5 extension + `HttpClientOperations` |
| `prozed-example` | A complete runnable sample app (REST + JDBC + JMS + filters + tests) |

The fastest way to learn Prozed is to read **`prozed-example`** end to end.

---

## Building from Source

```bash
git clone https://github.com/shaikezam/Prozed.git
cd Prozed
mvn clean install
```

Run the example app as a self-contained runnable jar:

```bash
# build the example (and the modules it depends on) into one fat jar
mvn -pl prozed-example -am clean package

# run it — no Maven needed at runtime
java -jar prozed-example/target/prozed-example-1.0-SNAPSHOT.jar
```

`prozed-example` configures the Shade plugin (see [Packaging & Deployment](#packaging--deployment)), so `package` emits a runnable uber-jar with the module service files merged.

Requires JDK 17+ and Maven 3.9+.

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
