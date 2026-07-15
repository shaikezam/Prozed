package prozed.io.jms.utils;

public class Constants {
    private Constants() {
    }

    public static final String JMS_BROKER_TYPE = "jms.broker.type";
    public static final String JMS_BROKER_URL = "jms.broker.url";
    public static final String JMS_USERNAME = "jms.username";
    public static final String JMS_PASSWORD = "jms.password";
    public static final String JMS_POOL_MAX_CONNECTIONS = "jms.pool.max-connections";
    public static final String JMS_POOL_MAX_SESSIONS_PER_CONNECTION = "jms.pool.max-sessions-per-connection";
    public static final String JMS_POOL_IDLE_TIMEOUT = "jms.pool.idle-timeout";
    public static final String JMS_REDELIVERY_MAX_REDELIVERIES = "jms.redelivery.max-redeliveries";
    public static final String JMS_REDELIVERY_INITIAL_DELAY = "jms.redelivery.initial-delay";
    public static final String JMS_REDELIVERY_BACKOFF_MULTIPLIER = "jms.redelivery.backoff-multiplier";

    public static final String DEFAULT_JMS_POOL_MAX_CONNECTIONS = "10";
    public static final String DEFAULT_JMS_POOL_MAX_SESSIONS_PER_CONNECTION = "500";
    public static final String DEFAULT_JMS_POOL_IDLE_TIMEOUT = "30000";
    public static final String DEFAULT_JMS_REDELIVERY_MAX_REDELIVERIES = "6";
    public static final String DEFAULT_JMS_REDELIVERY_INITIAL_DELAY = "1000";
    public static final String DEFAULT_JMS_REDELIVERY_BACKOFF_MULTIPLIER = "5.0";
}
