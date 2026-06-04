package prozed.io.core.internal.web;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.api.web.PayloadParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

final public class NodeRouter {
    private final Node root;
    private static final Logger logger = LoggerFactory.getLogger(NodeRouter.class);
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\{[^}]+}");

    public NodeRouter() {
        this.root = new Node("/");
    }

    public void addRoute(String path, Method handler, HttpMethod method) {
        validatePayloadParamCount(handler);
        Node current = root;
        String[] segments = path.split("/");
        int currentSegmentIndex = 0;
        for (; currentSegmentIndex < segments.length; currentSegmentIndex++) {
            if (segments[currentSegmentIndex].isEmpty()) {
                continue;
            }
            Optional<Node> node = findNodeForAddingRoute(current, segments[currentSegmentIndex]);
            if (node.isPresent()) {
                current = node.get();
            } else { // found node without relevant path
                break;
            }
        }
        buildSubTreeForAddingRoute(path, segments, currentSegmentIndex, current, handler, method);
        int i = 1;
    }

    public NodeExecutorWrapper lookup(HttpMethod method, String path, Map<String, String> queryParams) throws HttpException {
        String[] segments = path.split("/");
        final Map<String, String> pathParams = new HashMap<>();
        Node current = root;
        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            Optional<Node> staticChild = current.getStaticChild(segment);
            if (staticChild.isEmpty()) {
                Node wildCardChild = current.getWildCardChild();
                if (wildCardChild == null) {
                    String errorMessage = "%s path not found".formatted(path);
                    logger.debug(errorMessage);
                    throw new HttpException(errorMessage, HttpServletResponse.SC_NOT_FOUND);
                } else {
                    current = wildCardChild;
                    pathParams.put(wildCardChild.getPath(), segment);
                }
            } else {
                current = staticChild.get();
            }
        }
        Optional<Method> handler = current.getHandler(method);
        if (handler.isEmpty()) {
            String errorMessage = "%s method not found for path %s".formatted(method, path);
            logger.error(errorMessage);
            throw new HttpException(errorMessage, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        return new NodeExecutorWrapper(pathParams, queryParams, handler.get());
    }

    private void buildSubTreeForAddingRoute(
            String path,
            String[] segments,
            int currentSegmentIndex,
            Node current,
            Method handler,
            HttpMethod method) {
        for (; currentSegmentIndex < segments.length; currentSegmentIndex++) {
            String segment = segments[currentSegmentIndex];

            if (WILDCARD_PATTERN.matcher(segment).matches()) {
                Node existingWildcard = current.getWildCardChild();
                if (existingWildcard != null) {
                    if (!existingWildcard.getPath().equals(segment)) {
                        String conflictError = "Ambiguous wildcard route: path '%s' conflicts with existing wildcard '%s' at segment '%s'"
                                .formatted(path, existingWildcard.getPath(), segment);
                        logger.error(conflictError);
                        throw new IllegalStateException(conflictError);
                    }
                    current = existingWildcard;
                } else {
                    Optional<Node> existingStatic = current.getStaticChild(segment);
                    if (existingStatic.isPresent()) {
                        current = existingStatic.get();
                    } else {
                        Node node = new Node(segment);
                        current.setWildCardChild(node);
                        current = node;
                    }
                }
            } else {
                Node node = new Node(segment);
                current.addStaticChild(node);
                current = node;
            }
        }
        if (current.isHandlerExists(method)) {
            String errorMessage = "path {%s} with method {%s} already exists"
                    .formatted(path, method);
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        current.addHandler(method, handler);

    }

    private Optional<Node> findNodeForAddingRoute(Node parent, String segment) {
        if (parent == null) {
            return Optional.empty();
        }

        Optional<Node> staticChild = parent.getStaticChild(segment);
        if (staticChild.isPresent()) {
            return staticChild;
        }

        if (parent.getWildCardChild() != null &&
                parent.getWildCardChild().getPath().equals(segment)) {
            return Optional.of(parent.getWildCardChild());
        }

        return Optional.empty();
    }

    private void validatePayloadParamCount(Method method) {
        int payloadParamCount = 0;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(PayloadParam.class) != null) {
                payloadParamCount++;
            }
        }

        if (payloadParamCount > 1) {
            String errorMessage = "Method %s.%s has %d @PayloadParam annotations. Only one is allowed per method."
                    .formatted(method.getDeclaringClass().getSimpleName(), method.getName(), payloadParamCount);
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

    }
}
