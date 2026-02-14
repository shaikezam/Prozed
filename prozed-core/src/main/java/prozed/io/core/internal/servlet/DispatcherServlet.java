package prozed.io.core.internal.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.web.HttpException;
import prozed.io.core.internal.web.NodeExecutorWrapper;
import prozed.io.core.internal.web.NodeRouter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Front Controller for the Prozed framework.
 * It intercepts all requests and dispatches them to the appropriate controller method
 * based on the HTTP Verb and the Radix Tree path.
 */
public class DispatcherServlet extends HttpServlet {

    private final NodeRouter nodeRouter;
    private final ProzedContainer prozedContainer;
    private final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    public DispatcherServlet(
            final NodeRouter nodeRouter,
            final ProzedContainer prozedContainer
    ) {
        this.nodeRouter = nodeRouter;
        this.prozedContainer = prozedContainer;
    }

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
        try {
            Object controller = this.prozedContainer.get(nodeExecutorWrapper.method().getDeclaringClass());
            Object result = nodeExecutorWrapper.execute(
                    controller,
                    req.getReader().lines().collect(Collectors.joining(System.lineSeparator())),
                    this.gson);
            resp.setContentType(ContentType.APPLICATION_JSON.name());
            if (result != null) {
                resp.getWriter().write(gson.toJson(result));
            }
        } catch (Exception e) {
            logger.error("Exception while dispatching request", e);
            if (e instanceof HttpException exception) {
                exception.getHttpCode().applyTo(resp);
                resp.getWriter().write("""
                                {"error": "%s"}
                        """.formatted(e.getMessage()));
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}