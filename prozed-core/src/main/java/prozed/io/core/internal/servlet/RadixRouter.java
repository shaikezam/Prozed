package prozed.io.core.internal.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RadixRouter {

    /**
     * 1. the root is always /
     * 2. given path, we need to split it to segments "/"
     * 3.
     */
    private final RadixNode root = new RadixNode();

    public void addRoute(String verb, String path, Object controller, Method method) {
        RadixNode current = root;
        for (String segment : path.split("/")) {
            if (segment.isEmpty()) continue;
            if (segment.startsWith(":")) {
                if (current.paramChild == null) {
                    current.paramChild = new RadixNode();
                    current.paramName = segment.substring(1);
                }
                current = current.paramChild;
            } else {
                current = current.children.computeIfAbsent(segment, k -> new RadixNode());
            }
        }
        current.targets.put(verb.toUpperCase(), new RadixNode.RouteTarget(controller, method));
    }

    public Match lookup(String verb, String path) {
        RadixNode current = root;
        Map<String, String> params = new HashMap<>();

        for (String segment : path.split("/")) {
            if (segment.isEmpty()) continue;
            if (current.children.containsKey(segment)) {
                current = current.children.get(segment);
            } else if (current.paramChild != null) {
                params.put(current.paramName, segment);
                current = current.paramChild;
            } else {
                return null;
            }
        }

        RadixNode.RouteTarget target = current.targets.get(verb.toUpperCase());
        return target != null ? new Match(target, params) : null;
    }

    public record Match(RadixNode.RouteTarget target, Map<String, String> params) {}
}
