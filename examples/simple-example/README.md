![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

# Simple Example

A single-process [Prozed](../../README.md) application that exercises **every module of the framework** in one small, self-contained app. It is the fastest way to see how `prozed-core`, `prozed-jdbc`, `prozed-jms`, and `prozed-test` fit together.

Everything runs in one JVM: the web server, an in-memory database, and a message broker are all started from `Main` — no Docker, no external services, no setup.

---

## What it demonstrates

| Feature | Where | Notes |
|---|---|---|
| Annotation-based REST routing | `UserController` | `@Controller`, `@GetRequest`/`@PostRequest`/`@PutRequest`/`@DeleteRequest` |
| Path / query / payload binding | `UserController` | `@PathParam`, `@QueryParam`, `@PayloadParam` |
| Dependency injection | `UserService`, `UserRepository` | `@Bean` + `@Inject`, no XML |
| JDBC access & transactions | `UserRepository` | `JdbcOperations.select/selectOne/update` + `inTransaction(...)` |
| Flyway schema migrations | `db/migration/` | `V1__init.sql` creates `users`, `V2__insert.sql` seeds rows |
| JMS messaging | `UserService`, `MailListener` | `JmsOperations` produces, `@Listener` consumes the `mail` queue |
| Servlet filters | `ProtectPathFilter`, `MethodNotAllowedFilter` | cookie auth (`401`) and method guard (`405`) |
| Custom error responses | `UserController#getUser` | `HttpException` → `404` |
| Integration testing | `UserControllerTest` | `@ProzedTest` boots the real app and drives it over HTTP |

---

## Architecture

```
                 ┌──────────────────────────── JVM ────────────────────────────┐
                 │                                                              │
  HTTP  ───────► │  ProzedServer (:8080)                                        │
                 │    ├─ ProtectPathFilter        /protect                      │
                 │    ├─ MethodNotAllowedFilter   /api/v1/*                     │
                 │    └─ UserController ─► UserService ─► UserRepository ─► H2   │
                 │                              │                    (in-memory) │
                 │                              └─► JmsOperations ─┐             │
                 │                                                 ▼             │
                 │                              MailListener ◄── ActiveMQ broker │
                 │                                              (tcp://:61616)   │
                 │  H2 web console (:8082)                                       │
                 └──────────────────────────────────────────────────────────────┘
```

`Main` boots three things before starting the web server:

1. **H2 web console** on `http://localhost:8082` (browse the in-memory DB).
2. **Embedded ActiveMQ broker** on `tcp://localhost:61616` (non-persistent).
3. **ProzedServer** on `http://localhost:8080` with two filters registered.

---

## Prerequisites

- Java 17+
- Maven 3.9+

The Prozed modules are resolved from your local `~/.m2` repository, so build the framework once from the repo root before running this example:

```bash
mvn -f ../../pom.xml install -DskipTests
```

---

## Run

```bash
# from examples/simple-example
mvn package
java -jar target/simple-example-1.0.0-SNAPSHOT.jar
```

The server starts on port `8080`. Try it:

```bash
curl http://localhost:8080/health                       # OK
curl http://localhost:8080/user/2                        # {"id":2,"name":"Bob"}
curl http://localhost:8080/users/search?name=Bob&limit=10
curl -X POST http://localhost:8080/user \
     -H 'Content-Type: application/json' -d '{"name":"Dave"}'
```

---

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET`    | `/health`              | Liveness check, returns `OK` (`text/plain`) |
| `GET`    | `/user/{id}`           | Fetch one user, `404` if missing |
| `GET`    | `/users/search?name=&limit=` | Search by name (default limit 10) |
| `POST`   | `/user`                | Create a user, returns the new id |
| `POST`   | `/users`               | Bulk create in a single transaction |
| `PUT`    | `/user/{id}`           | Update a user |
| `DELETE` | `/user/{id}`           | Delete a user |
| `GET`    | `/protect`             | Guarded by `ProtectPathFilter` → `401` without a `sessionId` cookie |
| `GET`    | `/api/v1/test`         | Guarded by `MethodNotAllowedFilter` → `405` on `GET` |

---

## Tests

```bash
mvn test
```

`UserControllerTest` is annotated with `@ProzedTest(mainClass = Main.class, cleanUp = true)`: the extension boots the real application, waits for the port, runs each test against it over HTTP, and rolls back database state between tests. The test run binds to port `8081` (see `src/test/resources/prozed.properties`) so it never collides with a running dev instance.

---

## Configuration

`src/main/resources/prozed.properties`:

```properties
web.service.port=8080
web.service.scan-package=prozed.io.example
db.url=jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
db.driver-class-name=org.h2.Driver
db.username=sa
db.password=
flyway.enabled=true
jms.broker.type=activemq
jms.broker.url=tcp://localhost:61616
jms.username=admin
jms.password=admin
```

Swap the commented `db.url` line in the file to switch from in-memory to a file-backed H2 database that survives restarts.

---

## Project layout

```
simple-example/
├── pom.xml
└── src
    ├── main
    │   ├── java/prozed/io/example
    │   │   ├── Main.java                 # boots H2 console, ActiveMQ broker, ProzedServer
    │   │   ├── controller/UserController.java
    │   │   ├── service/UserService.java
    │   │   ├── repository/UserRepository.java
    │   │   ├── model/User.java
    │   │   ├── listener/MailListener.java
    │   │   └── web/{ProtectPathFilter,MethodNotAllowedFilter}.java
    │   └── resources
    │       ├── prozed.properties
    │       └── db/migration/{V1__init.sql,V2__insert.sql}
    └── test/java/prozed/io/example/controller/UserControllerTest.java
```

For the full framework reference, see the [root README](../../README.md).
