package com.example.project;

import org.junit.jupiter.api.Test;
import prozed.io.core.api.di.Inject;
import prozed.io.jdbc.JdbcOperations;
import prozed.io.test.api.ProzedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class ProjectServiceTest {

    @Inject
    private ProjectController projectController;

    private final JdbcOperations jdbc = new JdbcOperations();

    @Test
    public void testCreateProject() {
        String response = projectController.create(
            new ProjectController.ProjectRequest("APP", "Mobile App", "iOS and Android app."));

        assertEquals("Project APP created.", response);

        Long count = jdbc.selectOne("SELECT COUNT(*) AS c FROM projects WHERE project_key = ?",
            rs -> rs.getLong("c"), "APP");
        assertEquals(1L, count);
    }
}
