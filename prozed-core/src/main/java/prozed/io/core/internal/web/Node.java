package prozed.io.core.internal.web;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public record Node(String path,
                   Map<Method, HttpMethod> handlers,
                   List<Node> staticNodes,
                   Node paramNode) {
}
