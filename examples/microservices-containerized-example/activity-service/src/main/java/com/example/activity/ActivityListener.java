package com.example.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import com.google.gson.Gson;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.jdbc.JdbcOperations;
import prozed.io.jms.api.Listener;
import prozed.io.jms.api.DestinationType;

@Bean
@Listener(destination = "issues.queue", destinationType = DestinationType.QUEUE)
public class ActivityListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityListener.class);

    @Inject
    private JdbcOperations jdbc;

    private final Gson gson = new Gson();

    @Override
    public void onMessage(Message message) {
        try {
            String json = message.getBody(String.class);
            IssueEvent event = gson.fromJson(json, IssueEvent.class);
            log.info("Recording activity {} for {}", event.action(), event.issueKey());

            StringBuilder detail = new StringBuilder(event.action() == null ? "UPDATED" : event.action());
            if (event.type() != null) {
                detail.append(' ').append(event.type());
            }
            if (event.summary() != null) {
                detail.append(": ").append(event.summary());
            }

            jdbc.update("INSERT INTO activity (issue_key, action, detail) VALUES (?, ?, ?)",
                event.issueKey(), event.action(), detail.toString());
        } catch (Exception e) {
            log.error("Failed to record issue activity", e);
        }
    }

    public record IssueEvent(String issueKey, String projectKey, String type, String summary, String action) {}
}
