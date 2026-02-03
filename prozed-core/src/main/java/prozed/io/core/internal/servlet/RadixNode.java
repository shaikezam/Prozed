package prozed.io.core.internal.servlet;

import java.lang.reflect.Method;
import java.util.*;

class RadixNode {
    Map<String, RadixNode> children = new HashMap<>();
    RadixNode paramChild = null;
    String paramName = null;

    // New: Map HTTP Method (GET, POST) -> Target executable
    Map<String, RouteTarget> targets = new HashMap<>();

    public record RouteTarget(Object controller, Method method) {}
}