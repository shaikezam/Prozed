package prozed.io.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.FilterWrapper;
import prozed.io.core.api.web.ProzedServer;
import prozed.io.example.web.ProtectPathFilter;

import java.sql.SQLException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        try (ProzedServer server = new ProzedServer()) {
            server.addFilter(new FilterWrapper("protectFilter", "/protect", new ProtectPathFilter()));
            server.start();
        }
    }
}
