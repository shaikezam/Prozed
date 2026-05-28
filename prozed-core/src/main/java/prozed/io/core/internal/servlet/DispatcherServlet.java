package prozed.io.core.internal.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.ContentType;
import prozed.io.core.api.web.DeleteRequest;
import prozed.io.core.api.web.GetRequest;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.api.web.PostRequest;
import prozed.io.core.api.web.PutRequest;
import prozed.io.core.internal.di.ProzedContainer;
import prozed.io.core.internal.web.HttpException;
import prozed.io.core.internal.web.NodeExecutorWrapper;
import prozed.io.core.internal.web.NodeRouter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Map<String, String> queryParams = getQueryParam(req.getQueryString());

        // 2. Lookup the route in the Radix Tree
        // TODO normalize path
        try {
            NodeExecutorWrapper nodeExecutorWrapper = nodeRouter.lookup(HttpMethod.fromString(method), path, queryParams);
            Object controller = this.prozedContainer.get(nodeExecutorWrapper.method().getDeclaringClass());
            Object result = nodeExecutorWrapper.execute(
                    controller,
                    req.getReader().lines().collect(Collectors.joining(System.lineSeparator())),
                    this.gson);
            if (result != null) {
                Method handlerMethod = nodeExecutorWrapper.method();
                ContentType produced = resolveProducedContentType(handlerMethod);

                resp.setContentType(produced.value());
                if (produced == ContentType.TEXT_PLAIN) {
                    resp.getWriter().write(result.toString());
                } else {
                    resp.getWriter().write(gson.toJson(result));
                }
            }
        } catch (Exception e) {
            logger.error("Exception while dispatching request", e);
            if (e instanceof HttpException exception) {
                exception.getHttpCode().applyTo(resp);
                resp.setContentType(ContentType.APPLICATION_JSON.value());
                resp.getWriter().write("""
                                {"error": "%s"}
                        """.formatted(e.getMessage()));
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private ContentType resolveProducedContentType(Method method) {
        GetRequest getRequest = method.getAnnotation(GetRequest.class);
        if (getRequest != null) {
            return getRequest.produces();
        }

        PostRequest postRequest = method.getAnnotation(PostRequest.class);
        if (postRequest != null) {
            return postRequest.produces();
        }

        PutRequest putRequest = method.getAnnotation(PutRequest.class);
        if (putRequest != null) {
            return putRequest.produces();
        }

        DeleteRequest deleteRequest = method.getAnnotation(DeleteRequest.class);
        if (deleteRequest != null) {
            return deleteRequest.produces();
        }

        return ContentType.APPLICATION_JSON;
    }

    private Map<String, String> getQueryParam(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (Objects.isNull(query)) {
            return queryParams;
        }
        String[] queries = query.split("&");
        Stream.of(queries)
                .forEach(param -> {
                    String[] parts = param.split("=");
                    queryParams.put(parts[0], parts.length > 1 ? parts[1] : "");
                });
        return queryParams;
    }
}
