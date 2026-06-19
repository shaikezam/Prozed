package prozed.io.core.internal.web;

import prozed.io.core.api.web.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final public class Node {
    private String path;
    private final Map<String, Node> staticChildren = new HashMap<>();
    private final Map<HttpMethod, Method> handlers = new HashMap<>();
    private Node wildCardChild;

    public Node(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Optional<Method> getHandler(HttpMethod method) {
        return Optional.ofNullable(handlers.get(method));
    }

    public Node getWildCardChild() {
        return wildCardChild;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWildCardChild(Node wildCardChild) {
        this.wildCardChild = wildCardChild;
    }

    public void addHandler(HttpMethod httpMethod, Method handler) {
        if (handlers.containsKey(httpMethod)) {
            throw new IllegalStateException("Handler for " + httpMethod + " already exists");
        }
        this.handlers.put(httpMethod, handler);
    }

    public void addStaticChild(Node node) {
        staticChildren.put(node.getPath(), node);
    }

    public Optional<Node> getStaticChild(String path) {
        return Optional.ofNullable(staticChildren.get(path));
    }

    public boolean isHandlerExists(HttpMethod httpMethod) {
        return handlers.containsKey(httpMethod);
    }

    public boolean hasAnyHandler() {
        return !handlers.isEmpty();
    }
}
