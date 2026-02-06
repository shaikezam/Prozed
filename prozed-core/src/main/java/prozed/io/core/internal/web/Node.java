package prozed.io.core.internal.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private final String path;
    private final Map<Method, HttpMethod> handlers;
    private final List<Node> staticNodes;
    private Node paramNode;

    public Node(String path, 
               Map<Method, HttpMethod> handlers, 
               List<Node> staticNodes, 
               Node paramNode) {
        this.path = path;
        this.handlers = new HashMap<>(handlers);
        this.staticNodes = new ArrayList<>(staticNodes);
        this.paramNode = paramNode;
    }

    public void addHandler(Method method, HttpMethod httpMethod) {
        handlers.put(method, httpMethod);
    }
    
    public void addStaticNode(Node node) {
        staticNodes.add(node);
    }
    
    public void setParamNode(Node node) {
        paramNode = node;
    }
}
