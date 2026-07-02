![Java 21](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

# Microservices Containerized Example

A small **task tracker** built as three independent [Prozed](../../README.md) microservices, a shared message broker, a database-per-service topology, and a PHP web UI — all wired together with Docker Compose.

It shows how to take Prozed beyond a single process: **synchronous REST between a UI and services, asynchronous events between services over JMS, and per-service Flyway-managed databases**, each service packaged as a self-contained fat JAR and shipped as a container.

---

## Services

| Service | Stack | Database | Responsibility |
|---|---|---|---|
| **project-service**  | Prozed (core + jdbc)        | `project_db`  | CRUD for projects |
| **issue-service**    | Prozed (core + jdbc + jms)  | `issue_db`    | CRUD for issues; **publishes** issue events to JMS |
| **activity-service** | Prozed (core + jdbc + jms)  | `activity_db` | **Consumes** issue events and records an activity feed |
| **frontend-ui**      | PHP 8.2 + [FlightPHP](https://flightphp.com/) | — | Dashboard that aggregates all three services over HTTP |
| activemq             | Apache ActiveMQ Classic     | —             | Message broker for issue events |
| mariadb              | MariaDB 10.11               | all three     | One server hosting three isolated databases |
| phpmyadmin           | phpMyAdmin                  | —             | Browse the databases in a browser |

---

## Architecture

```
                  Browser
                     │  http://localhost:8092
                     ▼
            ┌──────────────────┐
            │   frontend-ui    │  PHP / FlightPHP
            │   (dashboard)    │
            └───┬─────┬─────┬──┘
        REST    │     │     │   REST (aggregates on each page load)
      ┌─────────┘     │     └─────────────┐
      ▼               ▼                    ▼
┌───────────┐   ┌───────────┐       ┌──────────────┐
│  project  │   │   issue   │       │   activity   │
│  service  │   │  service  │       │   service    │
└─────┬─────┘   └─────┬─────┘       └──────┬───────┘
      │               │  publish           │ consume
      │               │  IssueEvent        │ issues.queue
      │               ▼                    ▼
      │        ┌─────────────────────────────────┐
      │        │       ActiveMQ  (JMS queue)      │
      │        └─────────────────────────────────┘
      │               │                    │
      ▼               ▼                    ▼
┌────────────────────────────────────────────────┐
│   MariaDB:  project_db  │  issue_db  │ activity_db│
└────────────────────────────────────────────────┘
```

### Request flow

- **Create a project / issue** — the UI `POST`s to `project-service` / `issue-service` over HTTP.
- **Issue events** — when an issue is created or transitioned, `issue-service` publishes an `IssueEvent` to the `issues.queue` JMS queue and returns immediately. `activity-service` consumes those events asynchronously (via an `@Listener` bean) and writes them to its own activity feed. The two services never call each other directly — they are decoupled through the broker.
- **Dashboard** — on each page load the UI calls all three services and renders projects, the issue board, and the activity feed together.

### Database-per-service

Each service owns its schema and never touches another service's tables. The single MariaDB container hosts three separate databases (`project_db`, `issue_db`, `activity_db`), and each service runs its own Flyway migrations on startup with seed data.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker + Docker Compose

Build the Prozed framework into your local `~/.m2` once before launching, so the services can resolve the modules:

```bash
mvn -f ../../pom.xml install -DskipTests
```

---

## Run

```bash
# from examples/microservices-containerized-example
./start-cluster.sh
```

`start-cluster.sh` does the whole thing in order:

1. `composer install` for the PHP frontend.
2. `mvn clean package` each service into a fat JAR (`*-fat.jar`).
3. `docker-compose up --build` to start the broker, database, phpMyAdmin, the three services, and the UI — streaming all logs in the foreground (`Ctrl-C` stops the stack).

Then open:

| URL | What |
|---|---|
| http://localhost:8092 | Task tracker dashboard |
| http://localhost:8093 | phpMyAdmin (root / root) |

To tear everything down, including volumes and built images:

```bash
./stop-cluster.sh
```

---

## Service endpoints

These run on port `8080` **inside** each container (reached by service name on the Compose network — the frontend calls e.g. `http://issue-service:8080`). They are not published to the host by default.

**project-service**

| Method | Path | Description |
|---|---|---|
| `GET`  | `/projects/` | List all projects |
| `POST` | `/projects/` | Create a project |

**issue-service**

| Method | Path | Description |
|---|---|---|
| `GET`  | `/issues/`           | List all issues |
| `POST` | `/issues/`           | Create an issue (publishes an event) |
| `POST` | `/issues/transition` | Change an issue's status (publishes an event) |

**activity-service**

| Method | Path | Description |
|---|---|---|
| `GET` | `/activity/history` | Last 20 recorded activity entries |

---

## What to look for

- **Async decoupling** — create or move an issue in the UI, then watch the activity feed update. Tail `docker-compose logs -f activity-service` to see the `@Listener` bean consume the event off the queue.
- **Fat-jar packaging** — each `pom.xml` inherits the shade configuration from the parent and only declares its own `Main-Class`; the build produces a single runnable `*-fat.jar` that the container runs with `java -jar`.
- **Per-service migrations** — each service's `src/main/resources/db/migration` is applied by Flyway independently at boot.

---

## Project layout

```
microservices-containerized-example/
├── pom.xml                     # aggregator for the three services
├── docker-compose.yml          # broker, db, phpmyadmin, services, frontend
├── start-cluster.sh            # build + up
├── stop-cluster.sh             # down -v --rmi all
├── settings.xml                # forces deps through Maven Central, SNAPSHOTs from ~/.m2
├── project-service/
│   ├── pom.xml
│   └── src/main/java/com/example/project/{Main,ProjectController}.java
├── issue-service/
│   ├── pom.xml
│   └── src/main/java/com/example/issue/{Main,IssueController}.java
├── activity-service/
│   ├── pom.xml
│   └── src/main/java/com/example/activity/{Main,ActivityController,ActivityListener}.java
└── frontend-ui/                # FlightPHP dashboard
    ├── public/index.php        # routes + service aggregation
    └── views/dashboard.php     # rendered board
```

For the full framework reference, see the [root README](../../README.md). For a single-process tour of every module, see [simple-example](../simple-example/README.md).
