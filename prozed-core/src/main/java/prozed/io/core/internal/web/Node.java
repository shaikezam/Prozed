package prozed.io.core.internal.web;

import java.lang.reflect.Method;
import java.util.List;

public class Node {
    private String path;
    private List<Node> staticChildren;
    private Node wildCardChild;
    private Method handler;
    
    public String getPath() {
        return path;
    }

    public List<Node> getStaticChildren() {
        return staticChildren;
    }

    public Node getWildCardChild() {
        return wildCardChild;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setStaticChildren(List<Node> staticChildren) {
        this.staticChildren = staticChildren;
    }
    
    public void setWildCardChild(Node wildCardChild) {
        this.wildCardChild = wildCardChild;
    }
    
    public void setHandler(Method handler) {
        this.handler = handler;
    }
    
    public Method getHandler() {
        return handler;
    }
}
