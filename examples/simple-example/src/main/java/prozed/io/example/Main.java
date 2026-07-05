package prozed.io.example;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.FilterWrapper;
import prozed.io.core.api.web.ProzedServer;
import prozed.io.example.web.MethodNotAllowedFilter;
import prozed.io.example.web.ProtectPathFilter;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        org.h2.tools.Server h2 = org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.start();

        // Closed in reverse order after the server stops, releasing 61616 / 8082 — so a
        // second @ProzedTest class in the same JVM can re-bind them.
        try (AutoCloseable h2Closer = h2::stop;
             AutoCloseable brokerCloser = broker::stop;
             ProzedServer server = new ProzedServer()) {
            server.addFilter(new FilterWrapper("protectFilter", "/protect", new ProtectPathFilter()));
            server.addFilter(new FilterWrapper("methodNotAllowedFilter", "/api/v1/*", new MethodNotAllowedFilter()));
            server.start();
        }
    }
}
