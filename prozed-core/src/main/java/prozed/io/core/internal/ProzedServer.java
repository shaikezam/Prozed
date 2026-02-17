package prozed.io.core.internal;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import prozed.io.core.internal.servlet.DispatcherServlet;
import prozed.io.core.internal.servlet.WebScanner;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.web.NodeRouter;

import java.io.Closeable;
import java.io.File;

final public class ProzedServer implements Closeable {
    private final Tomcat tomcat;
    private final int port;
    private final String contextPath;
    private final String scanPackage; // New field to know what to scan
    private ProzedContainer container;

    public ProzedContainer getContainer() {
        return container;
    }

    private ProzedServer(final Builder builder) {
        this.port = builder.port;
        this.contextPath = builder.contextPath;
        this.scanPackage = builder.scanPackage;
        this.tomcat = new Tomcat();
        setupTomcat();
    }

    private void setupTomcat() {
        tomcat.setPort(port);

        // Isolate Tomcat metadata to system temp
        final String baseDir = new File(System.getProperty("java.io.tmpdir"), "prozed-tomcat-" + port).getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        // MUST trigger connector before start()
        tomcat.getConnector();

        // InitializFlightContainere the Prozed Engine
        final NodeRouter nodeRouter = new NodeRouter();
        container = new ProzedContainer(scanPackage);
        final WebScanner scanner = new WebScanner(nodeRouter);

        // Scan the user-provided package
        if (scanPackage != null && !scanPackage.isEmpty()) {
            scanner.scan(scanPackage);
        }

        // Setup Tomcat Context
        final String fakeDocBase = new File(baseDir, "webapps").getAbsolutePath();
        new File(fakeDocBase).mkdirs();

        final Context ctx = tomcat.addContext(contextPath, fakeDocBase);

        // Link the Router to the Dispatcher
        final DispatcherServlet dispatcher = new DispatcherServlet(nodeRouter, container);
        Tomcat.addServlet(ctx, "prozedDispatcher", dispatcher);
        ctx.addServletMappingDecoded("/*", "prozedDispatcher");
    }

    public void start() {
        try {
            tomcat.start();
            System.out.println("ProzedServer started on port " + port);
            System.out.println("Scanning package: " + scanPackage);
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

    public static class Builder {
        private int port = 8080;
        private String contextPath = "";
        private String scanPackage = ""; // Store the package to scan

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder scan(String packageName) {
            this.scanPackage = packageName;
            return this;
        }

        public Builder withContextPath(String contextPath) {
            this.contextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
            return this;
        }

        public ProzedServer build() {
            return new ProzedServer(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}