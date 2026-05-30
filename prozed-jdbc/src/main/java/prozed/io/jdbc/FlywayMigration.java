package prozed.io.jdbc;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;

import static prozed.io.jdbc.utils.Constants.*;

@Bean
public class FlywayMigration {

    private static final Logger logger = LoggerFactory.getLogger(FlywayMigration.class);

    @Inject
    private JdbcOperations jdbcOperations;

    public FlywayMigration() {
        if (!Boolean.parseBoolean(ProzedPropertiesWrapper.getProperty(FLYWAY_ENABLED, "false"))) {
            logger.info("Flyway migration disabled");
            return;
        }

        Flyway flyway = Flyway.configure()
                .dataSource(
                        ProzedPropertiesWrapper.getProperty(DB_URL),
                        ProzedPropertiesWrapper.getProperty(DB_USERNAME),
                        ProzedPropertiesWrapper.getProperty(DB_PASSWORD)
                )
                .locations(ProzedPropertiesWrapper.getProperty(FLYWAY_LOCATIONS, "classpath:db/migration"))
                .baselineOnMigrate(Boolean.parseBoolean(ProzedPropertiesWrapper.getProperty(FLYWAY_BASELINE_ON_MIGRATE, "true")))
                .table(ProzedPropertiesWrapper.getProperty(FLYWAY_TABLE, "flyway_schema_history"))
                .load();

        int applied = flyway.migrate().migrationsExecuted;
        logger.info("Flyway applied {} migration(s)", applied);
    }
}