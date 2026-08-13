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
@Listener(destination = "activity.topic", destinationType = DestinationType.TOPIC,
    durable = true, subscriptionName = "activity-service-sub")
public class ActivityTopicListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityTopicListener.class);

    @Inject
    private JdbcOperations jdbc;

    private final Gson gson = new Gson();

    @Override
    public void onMessage(Message message) {
        try {
            String json = message.getBody(String.class);
            ActivityListener.IssueEvent event = gson.fromJson(json, ActivityListener.IssueEvent.class);
            log.info("Recording broadcast activity {} for {}", event.action(), event.issueKey());

            jdbc.update("INSERT INTO activity (issue_key, action, detail) VALUES (?, ?, ?)",
                event.issueKey(), event.action(), "BROADCAST " + event.action());
        } catch (Exception e) {
            log.error("Failed to record broadcast activity", e);
        }
    }
}
