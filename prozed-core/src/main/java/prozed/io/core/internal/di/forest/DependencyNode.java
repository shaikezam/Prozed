package prozed.io.core.internal.di.forest;

import java.util.Set;

public record DependencyNode(Class<?> clazz, Set<DependencyNode> nodes) {
}
