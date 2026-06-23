package com.example.issue;

import org.junit.jupiter.api.Test;
import prozed.io.core.api.di.Inject;
import prozed.io.jdbc.JdbcOperations;
import prozed.io.test.api.ProzedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ProzedTest(mainClass = Main.class, cleanUp = true)
public class IssueServiceTest {

    @Inject
    private IssueController issueController;

    private final JdbcOperations jdbc = new JdbcOperations();

    @Test
    public void testCreateIssueGeneratesKey() {
        // PROZ has 5 seeded issues, so the next key is PROZ-6.
        String key = issueController.createIssue(new IssueController.IssueRequest(
            "PROZ", "TASK", "Add rate limiting", "Throttle login attempts.", "PROZ-1", "me", "HIGH"));

        assertEquals("PROZ-6", key);

        String summary = jdbc.selectOne("SELECT summary FROM issues WHERE issue_key = ?",
            rs -> rs.getString("summary"), key);
        assertEquals("Add rate limiting", summary);

        String status = jdbc.selectOne("SELECT status FROM issues WHERE issue_key = ?",
            rs -> rs.getString("status"), key);
        assertEquals("TODO", status);
    }

    @Test
    public void testTransitionUpdatesStatus() {
        issueController.transition(new IssueController.TransitionRequest("PROZ-2", "DONE"));

        String status = jdbc.selectOne("SELECT status FROM issues WHERE issue_key = ?",
            rs -> rs.getString("status"), "PROZ-2");
        assertEquals("DONE", status);
    }
}
