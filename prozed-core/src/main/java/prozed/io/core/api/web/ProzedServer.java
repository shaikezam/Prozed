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
import java.util.HashMap;
import java.util.Map;

public class ProzedServer implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProzedServer.class);

    private final Tomcat tomcat;
    private ProzedContainer container;
    private final Map<String, FilterWrapper> filters = new HashMap<>();

    public ProzedContainer getContainer() {
        return container;
    }

    public ProzedServer() {
        this.tomcat = new Tomcat();
    }

    public void start() {
        try {
            setupTomcat();
            tomcat.start();
            LOGGER.info("ProzedServer started on port {}", ProzedPropertiesWrapper.getServicePort());
            LOGGER.info("Scanning package: {}", ProzedPropertiesWrapper.getScanPackage());
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
        try {
            if (tomcat.getServer() != null && tomcat.getServer().getState().isAvailable()) {
                tomcat.stop();
                tomcat.destroy();
                System.out.println("ProzedServer shut down successfully.");
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
        container = new ProzedContainer(ProzedPropertiesWrapper.getScanPackage());
        WebScanner scanner = new WebScanner(nodeRouter);

        // Scan the user-provided package
        if (ProzedPropertiesWrapper.getScanPackage() != null && !ProzedPropertiesWrapper.getScanPackage().isEmpty()) {
            scanner.scan(ProzedPropertiesWrapper.getScanPackage());
        }
        String fakeDocBase = new File(baseDir, "webapps").getAbsolutePath();
        new File(fakeDocBase).mkdirs();
        Context ctx = tomcat.addContext("/", fakeDocBase);
        addFilter(ctx);
        DispatcherServlet dispatcher = new DispatcherServlet(nodeRouter, container);
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