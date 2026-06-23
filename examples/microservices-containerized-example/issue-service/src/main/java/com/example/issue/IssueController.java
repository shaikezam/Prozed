package com.example.issue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.*;
import prozed.io.jdbc.JdbcOperations;
import prozed.io.jms.JmsOperations;
import prozed.io.jms.api.DestinationType;

@Bean
@Controller(path = "/issues")
public class IssueController {

    private static final Logger log = LoggerFactory.getLogger(IssueController.class);

    @Inject
    private JdbcOperations jdbc;

    @Inject
    private JmsOperations jms;

    @GetRequest(value = "/")
    public List<Issue> list() {
        return jdbc.select(
            "SELECT issue_key, project_key, type, summary, description, status, parent_key, assignee, priority "
                + "FROM issues ORDER BY id",
            rs -> new Issue(
                rs.getString("issue_key"), rs.getString("project_key"), rs.getString("type"),
                rs.getString("summary"), rs.getString("description"), rs.getString("status"),
                rs.getString("parent_key"), rs.getString("assignee"), rs.getString("priority")));
    }

    @PostRequest(value = "/")
    public String createIssue(@PayloadParam IssueRequest request) {
        Long existing = jdbc.selectOne("SELECT COUNT(*) AS c FROM issues WHERE project_key = ?",
            rs -> rs.getLong("c"), request.projectKey());
        String issueKey = request.projectKey() + "-" + (existing + 1);

        String priority = (request.priority() == null || request.priority().isBlank()) ? "MEDIUM" : request.priority();
        log.info("Creating {} {} under project {}", request.type(), issueKey, request.projectKey());

        jdbc.update(
            "INSERT INTO issues (issue_key, project_key, type, summary, description, status, parent_key, assignee, priority) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            issueKey, request.projectKey(), request.type(), request.summary(), request.description(),
            "TODO", request.parentKey(), request.assignee(), priority);

        IssueEvent event = new IssueEvent(issueKey, request.projectKey(), request.type(), request.summary(), "CREATED");
        jms.sendMessage(event, "issues.queue", DestinationType.QUEUE);

        return issueKey;
    }

    @PostRequest(value = "/transition")
    public String transition(@PayloadParam TransitionRequest request) {
        jdbc.update("UPDATE issues SET status = ? WHERE issue_key = ?", request.status(), request.issueKey());

        IssueEvent event = new IssueEvent(request.issueKey(), null, null, null, "STATUS_" + request.status());
        jms.sendMessage(event, "issues.queue", DestinationType.QUEUE);

        return "Issue " + request.issueKey() + " moved to " + request.status() + ".";
    }

    public record IssueRequest(String projectKey, String type, String summary, String description,
                               String parentKey, String assignee, String priority) {}
    public record TransitionRequest(String issueKey, String status) {}
    public record IssueEvent(String issueKey, String projectKey, String type, String summary, String action) {}
    public record Issue(String issueKey, String projectKey, String type, String summary, String description,
                        String status, String parentKey, String assignee, String priority) {}
}
