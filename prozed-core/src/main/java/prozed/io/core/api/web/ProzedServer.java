package prozed.io.core.api.web;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.core.internal.servlet.DispatcherServlet;
import prozed.io.core.internal.servlet.WebScanner;
import prozed.io.core.internal.web.NodeRouter;

import java.io.Closeable;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProzedServer implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProzedServer.class);

    private final Tomcat tomcat;
    private static ProzedContainer CONTAINER = new ProzedContainer();
    private static volatile ProzedServer CURRENT;
    private final Map<String, FilterWrapper> filters = new LinkedHashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public static ProzedContainer getContainer() {
        return CONTAINER;
    }

    public static void resetContainer() {
        CONTAINER = new ProzedContainer();
    }

    /**
     * Close the server currently running in this JVM, if any. {@code start()} blocks in
     * Tomcat's await loop, which does not unwind on thread interrupt — so a test harness
     * needs this to stop the server (release its port) deterministically between runs.
     */
    public static void shutdownCurrent() {
        ProzedServer server = CURRENT;
        if (server != null) {
            server.close();
        }
    }

    public ProzedServer() {
        this.tomcat = new Tomcat();
        CURRENT = this;
    }

    public void start() {
        try {
            setupTomcat();
            tomcat.start();
            LOGGER.info("ProzedServer started on port {}", ProzedPropertiesWrapper.getServicePort());
            LOGGER.info("Scanning package: {}", ProzedPropertiesWrapper.getScanPackage());
            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException("Failed to start ProzedServer", e);
        }
    }

    public void addFilter(FilterWrapper filterWrapper) {
        filters.putIfAbsent(filterWrapper.name(), filterWrapper);
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        if (CURRENT == this) {
            CURRENT = null;
        }
        try {
            if (tomcat.getServer() != null && tomcat.getServer().getState().isAvailable()) {
                getContainer().preDestroy();
                getContainer().postDestroy();
                tomcat.stop();
                tomcat.destroy();
                LOGGER.info("ProzedServer shut down successfully.");
            }
        } catch (LifecycleException e) {
            throw new RuntimeException("Failed to close ProzedServer", e);
        }
    }

    private void setupTomcat() {
        tomcat.setPort(ProzedPropertiesWrapper.getServicePort());
        String baseDir = new File(System.getProperty("java.io.tmpdir"), "prozed-tomcat-" + ProzedPropertiesWrapper.getServicePort()).getAbsolutePath();
        tomcat.setBaseDir(baseDir);
        tomcat.getConnector();
        NodeRouter nodeRouter = new NodeRouter();
        CONTAINER.init(ProzedPropertiesWrapper.getScanPackage());
        WebScanner scanner = new WebScanner(nodeRouter);

        // Scan the user-provided package
        if (ProzedPropertiesWrapper.getScanPackage() != null && !ProzedPropertiesWrapper.getScanPackage().isEmpty()) {
            scanner.scan(ProzedPropertiesWrapper.getScanPackage());
        }
        String fakeDocBase = new File(baseDir, "webapps").getAbsolutePath();
        new File(fakeDocBase).mkdirs();
        Context ctx = tomcat.addContext("/", fakeDocBase);
        addFilter(ctx);
        DispatcherServlet dispatcher = new DispatcherServlet(nodeRouter, CONTAINER);
        Tomcat.addServlet(ctx, "prozedDispatcher", dispatcher);
        ctx.addServletMappingDecoded("/*", "prozedDispatcher");
    }

    private void addFilter(Context ctx) {
        for (FilterWrapper filterWrapper : filters.values()) {
            FilterDef def = new FilterDef();
            def.setFilterName(filterWrapper.name());
            def.setFilter(filterWrapper.filter());
            ctx.addFilterDef(def);

            FilterMap map = new FilterMap();
            map.setFilterName(filterWrapper.name());
            map.addURLPattern(filterWrapper.urlPattern());
            ctx.addFilterMap(map);
        }
    }


}