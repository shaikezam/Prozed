package prozed.io.core.api.web;

import jakarta.servlet.Filter;

public record FilterWrapper(
        String name,
        String urlPattern,
        Filter filter
) {
}
