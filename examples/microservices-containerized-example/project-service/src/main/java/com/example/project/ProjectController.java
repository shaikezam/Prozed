package com.example.project;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.*;
import prozed.io.jdbc.JdbcOperations;

@Bean
@Controller(path = "/projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Inject
    private JdbcOperations jdbc;

    @GetRequest(value = "/")
    public List<Project> list() {
        return jdbc.select("SELECT project_key, name, description FROM projects ORDER BY id",
            rs -> new Project(rs.getString("project_key"), rs.getString("name"), rs.getString("description")));
    }

    @PostRequest(value = "/", produces = ContentType.TEXT_PLAIN)
    public String create(@PayloadParam ProjectRequest request) {
        log.info("Creating project: {}", request.projectKey());

        jdbc.update("INSERT INTO projects (project_key, name, description) VALUES (?, ?, ?)",
            request.projectKey(), request.name(), request.description());

        return "Project " + request.projectKey() + " created.";
    }

    public record ProjectRequest(String projectKey, String name, String description) {}
    public record Project(String projectKey, String name, String description) {}
}
