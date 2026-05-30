package prozed.io.jdbc.utils;

public class Constants {
    private Constants() {
    }

    // Database - Connection
    public static final String DB_URL = "db.url";
    public static final String DB_DRIVER_CLASS_NAME = "db.driver-class-name";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";

    // Database - Pool Sizing
    public static final String DB_POOL_INITIAL_SIZE = "db.pool.initial-size";
    public static final String DB_POOL_MIN_IDLE = "db.pool.min-idle";
    public static final String DB_POOL_MAX_IDLE = "db.pool.max-idle";
    public static final String DB_POOL_MAX_ACTIVE = "db.pool.max-active";

    // Database - Timeout
    public static final String DB_POOL_MAX_WAIT = "db.pool.max-wait";

    // Database - Validation
    public static final String DB_POOL_TEST_ON_BORROW = "db.pool.test-on-borrow";
    public static final String DB_POOL_VALIDATION_QUERY = "db.pool.validation-query";
    public static final String DB_POOL_VALIDATION_INTERVAL = "db.pool.validation-interval";

    // Database - Abandonment
    public static final String DB_POOL_REMOVE_ABANDONED = "db.pool.remove-abandoned";
    public static final String DB_POOL_REMOVE_ABANDONED_TIMEOUT = "db.pool.remove-abandoned-timeout";
    public static final String DB_POOL_LOG_ABANDONED = "db.pool.log-abandoned";

    // Flyway
    public static final String FLYWAY_ENABLED = "flyway.enabled";
    public static final String FLYWAY_LOCATIONS = "flyway.locations";
    public static final String FLYWAY_BASELINE_ON_MIGRATE = "flyway.baseline-on-migrate";
    public static final String FLYWAY_TABLE = "flyway.table";

}
