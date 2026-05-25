package prozed.io.core.internal.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.*;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.reflection.PackageScanner;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.internal.web.NodeRouter;

import java.lang.reflect.Method;
import java.util.Set;

public class WebScanner {
    private static final Logger logger = LoggerFactory.getLogger(WebScanner.class);
    private final NodeRouter nodeRouter;
    private final PackageScanner packageScanner = new PackageScanner();

    public WebScanner(NodeRouter nodeRouter) {
        this.nodeRouter = nodeRouter;
    }

    /**
     * Entry point: Scans a package, finds controllers, and populates the router.
     */
    public void scan(String packageName) {
        try {
            Set<Class<?>> classes = packageScanner.scan(packageName, Controller.class);
            for (Class<?> clazz : classes) {
                // 2. Get the base path from @Controller("/base")
                String basePath = clazz.getAnnotation(Controller.class).path();

                // 3. Scan methods for Request annotations
                for (Method method : clazz.getDeclaredMethods()) {
                    registerRouteIfPresent(basePath, method);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Prozed: Failed to scan package " + packageName, e);
        }
    }

    private void registerRouteIfPresent(String basePath, Method method) {
        String subPath = null;
        HttpMethod httpMethod = null;

        if (method.isAnnotationPresent(GetRequest.class)) {
            subPath = method.getAnnotation(GetRequest.class).value();
            httpMethod = HttpMethod.GET;
        } else if (method.isAnnotationPresent(PostRequest.class)) {
            subPath = method.getAnnotation(PostRequest.class).value();
            httpMethod = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PutRequest.class)) {
            subPath = method.getAnnotation(PutRequest.class).value();
            httpMethod = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DeleteRequest.class)) {
            subPath = method.getAnnotation(DeleteRequest.class).value();
            httpMethod = HttpMethod.DELETE;
        }

        if (subPath != null) {
            String fullPath = normalizePath(basePath, subPath);
            // Add to the Radix Tree for O(k) lookup time
            nodeRouter.addRoute(fullPath, method, httpMethod);
            logger.info("Mapped " + httpMethod + " " + fullPath + " -> " + method.getName());
        }
    }

    private String normalizePath(String base, String sub) {
        // Ensures result always starts with / and has no double slashes
        String combined = "/" + base + "/" + sub;
        return combined.replaceAll("/{2,}", "/");
    }
}