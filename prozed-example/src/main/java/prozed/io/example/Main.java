package prozed.io.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.internal.ProzedServer;
import prozed.io.jdbc.JdbcOperations;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        try (ProzedServer server = new ProzedServer()) {
//            JdbcOperations jdbc = (JdbcOperations) server.getContainer().get(JdbcOperations.class);
//            try (Connection conn = jdbc.getDataSource().getConnection()) {
//                logger.info("DB connected: {}", conn.getMetaData().getURL());
//            }
            server.start();
        }
    }
}
