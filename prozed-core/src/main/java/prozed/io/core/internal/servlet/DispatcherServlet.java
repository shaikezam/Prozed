package prozed.io.core.internal.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import prozed.io.core.api.web.Produces;
import prozed.io.core.internal.web.HttpMethod;
import prozed.io.core.internal.web.Node;
import prozed.io.core.internal.web.NodeExecutorWrapper;
import prozed.io.core.internal.web.NodeRouter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The Front Controller for the Prozed framework.
 * It intercepts all requests and dispatches them to the appropriate controller method
 * based on the HTTP Verb and the Radix Tree path.
 */
public class DispatcherServlet extends HttpServlet {

    private final NodeRouter nodeRouter;

    public DispatcherServlet(final NodeRouter nodeRouter) {
        this.nodeRouter = nodeRouter;
    }

    /**
     * Overriding service() allows us to handle all HTTP methods (GET, POST, PUT, DELETE)
     * through a single routing entry point.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. Normalize the path from Tomcat
        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        String method = req.getMethod(); // GET, POST, etc.
        Map<String, String[]> queryParamsArray = req.getParameterMap();
        Map<String, String> queryParams = new HashMap<>();

        for (Map.Entry<String, String[]> entry : queryParamsArray.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            // Take first value (most common case)
            String value = values.length > 0 ? values[0] : "";
            queryParams.put(key, value);
        }


        // 2. Lookup the route in the Radix Tree
        // TODO normalize path
        NodeExecutorWrapper nodeExecutorWrapper = nodeRouter.lookup(HttpMethod.fromString(method), path, queryParams);

        if (match != null) {
            try {
                // 3. Handle Content-Type via @Produces annotation
                Method targetMethod = match.target().method();
                if (targetMethod.isAnnotationPresent(Produces.class)) {
                    resp.setContentType(targetMethod.getAnnotation(Produces.class).value());
                } else {
                    resp.setContentType("application/json"); // Default for Prozed
                }
                resp.setCharacterEncoding("UTF-8");

                // 4. Invoke the method with Parameters if needed
                Object result;
                if (targetMethod.getParameterCount() > 0) {
                    // We pass the params map (e.g., {"id": "123"}) extracted by the RadixRouter
                    result = targetMethod.invoke(match.target().controller(), match.params());
                } else {
                    result = targetMethod.invoke(match.target().controller());
                }

                // 5. Write the response
                if (result != null) {
                    resp.getWriter().write(result.toString());
                }

            } catch (Exception e) {
                // Get the actual exception thrown by the controller
                Throwable cause = (e.getCause() != null) ? e.getCause() : e;
                System.err.println("Prozed Execution Error on " + method + " " + path);
                cause.printStackTrace();

                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Internal Server Error\", \"detail\": \"" + cause.getMessage() + "\"}");
            }
        } else {
            // 6. 404 Handling with Debugging info
            System.out.println("Prozed 404: No match for [" + method + "] " + path);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Not Found\", \"path\": \"" + path + "\", \"method\": \"" + method + "\"}");
        }
    }
}