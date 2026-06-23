package com.example.activity;

import java.util.List;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.api.web.*;
import prozed.io.jdbc.JdbcOperations;

@Bean
@Controller(path = "/activity")
public class ActivityController {

    @Inject
    private JdbcOperations jdbc;

    @GetRequest(value = "/history")
    public List<ActivityRecord> getHistory() {
        return jdbc.select("SELECT issue_key, action, detail, logged_at FROM activity ORDER BY id DESC LIMIT 20",
            rs -> new ActivityRecord(rs.getString("issue_key"), rs.getString("action"),
                rs.getString("detail"), rs.getString("logged_at")));
    }

    public record ActivityRecord(String issueKey, String action, String detail, String loggedAt) {}
}
