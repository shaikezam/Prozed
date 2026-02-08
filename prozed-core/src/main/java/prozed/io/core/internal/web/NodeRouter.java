package prozed.io.core.internal.web;

import java.lang.reflect.Method;
import java.util.Optional;

public class NodeRouter {
    private Node root;

    public NodeRouter() {
        this.root = new Node();
    }

    public void addRoute(String path, Method handler) {
        Node current = root;
        Optional<String> lcp = findLCP(path, current.getPath());
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (current.getPath().equals(segment)) {
                c
            }
            if (segment.equals(current.getPath())) {

            }
        }
    }

    private Optional<Node> findNode(Node parent, String segment) {
        if (parent.getPath().equals(segment)) {
            return Optional.of(parent);
        }
        for (Node child : parent.getStaticChildren()) {
            Optional<Node> result = findNode(child, segment);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
