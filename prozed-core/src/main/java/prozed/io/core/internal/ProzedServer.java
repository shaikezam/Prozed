package prozed.io.core.internal;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.core.internal.servlet.DispatcherServlet;
import prozed.io.core.internal.servlet.WebScanner;
import prozed.io.core.internal.web.NodeRouter;

import java.io.Closeable;
import java.io.File;

public class ProzedServer implements Closeable {
    private final Tomcat tomcat;
    private final ProzedPropertiesWrapper properties = new ProzedPropertiesWrapper();
    private ProzedContainer container;
    private static final Logger logger = LoggerFactory.getLogger(ProzedServer.class);

    public ProzedContainer getContainer() {
        return container;
    }

    public ProzedServer() {
        this.tomcat = new Tomcat();
        setupTomcat();
    }

    public void start() {
        try {
            tomcat.start();
            logger.info("ProzedServer started on port {}", properties.getServicePort());
            logger.info("Scanning package: {}", properties.getScanPackage());
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException("Failed to start ProzedServer", e);
        }
    }

    @Override
    public void close() {
        try {
            if (tomcat.getServer() != null && tomcat.getServer().getState().isAvailable()) {
                tomcat.stop();
                tomcat.destroy();
                System.out.println("ProzedServer shut down successfully.");
            }
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private void setupTomcat() {
        tomcat.setPort(properties.getServicePort());
        String baseDir = new File(System.getProperty("java.io.tmpdir"), "prozed-tomcat-" + properties.getServicePort()).getAbsolutePath();
        tomcat.setBaseDir(baseDir);
        tomcat.getConnector();
        NodeRouter nodeRouter = new NodeRouter();
        container = new ProzedContainer(properties.getScanPackage());
        WebScanner scanner = new WebScanner(nodeRouter);

        // Scan the user-provided package
        if (properties.getScanPackage() != null && !properties.getScanPackage().isEmpty()) {
            scanner.scan(properties.getScanPackage());
        }
        String fakeDocBase = new File(baseDir, "webapps").getAbsolutePath();
        new File(fakeDocBase).mkdirs();
        Context ctx = tomcat.addContext("/", fakeDocBase);
        DispatcherServlet dispatcher = new DispatcherServlet(nodeRouter, container);
        Tomcat.addServlet(ctx, "prozedDispatcher", dispatcher);
        ctx.addServletMappingDecoded("/*", "prozedDispatcher");
    }

}