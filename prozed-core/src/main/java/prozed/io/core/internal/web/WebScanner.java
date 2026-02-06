package prozed.io.core.internal.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.*;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.reflaction.PackageScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebScanner {
    private final RadixRouter router;
    private final ProzedContainer container;
    private final PackageScanner packageScanner = new PackageScanner();
    private static final Logger logger = LoggerFactory.getLogger(WebScanner.class);

    public WebScanner(RadixRouter router, ProzedContainer container) {
        this.router = router;
        this.container = container;
    }

    /**
     * Entry point: Scans a package, finds controllers, and populates the router.
     */
    public void scan(String packageName) {
        try {
            Set<Class<?>> classes = packageScanner.scan(packageName, Controller.class);
            for (Class<?> clazz : classes) {
                // 1. Get/Create the controller instance via the DI Container
                Object instance = container.get(clazz);

                // 2. Get the base path from @Controller("/base")
                String basePath = clazz.getAnnotation(Controller.class).path();

                // 3. Scan methods for Request annotations
                for (Method method : clazz.getDeclaredMethods()) {
                    registerRouteIfPresent(basePath, instance, method);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Prozed: Failed to scan package " + packageName, e);
        }
    }

    private void registerRouteIfPresent(String basePath, Object instance, Method method) {
        String subPath = null;
        String httpMethod = null;

        if (method.isAnnotationPresent(GetRequest.class)) {
            subPath = method.getAnnotation(GetRequest.class).path();
            httpMethod = HttpMethod.GET.name();
        } else if (method.isAnnotationPresent(PostRequest.class)) {
            subPath = method.getAnnotation(PostRequest.class).path();
            httpMethod = HttpMethod.POST.name();
        } else if (method.isAnnotationPresent(PutRequest.class)) {
            subPath = method.getAnnotation(PutRequest.class).path();
            httpMethod = HttpMethod.PUT.name();
        } else if (method.isAnnotationPresent(DeleteRequest.class)) {
            subPath = method.getAnnotation(DeleteRequest.class).path();
            httpMethod = HttpMethod.DELETE.name();
        }

        if (subPath != null) {
            String fullPath = normalizePath(basePath, subPath);
            // Add to the Radix Tree for O(k) lookup time
            router.addRoute(httpMethod, fullPath, instance, method);
            logger.info("Mapped " + httpMethod + " " + fullPath + " -> " + method.getName());
        }
    }

    private String normalizePath(String base, String sub) {
        // Ensures result always starts with / and has no double slashes
        String combined = "/" + base + "/" + sub;
        return combined.replaceAll("/{2,}", "/");
    }

    /**
     * Simplified class discovery logic for local development.
     */
    private List<Class<?>> findClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource == null) return classes;

        File directory = new File(resource.getFile());
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }
}