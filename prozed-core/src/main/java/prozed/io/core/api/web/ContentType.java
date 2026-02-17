package prozed.io.core.api.web;

public enum ContentType {
    APPLICATION_JSON("application/json");

    private final String name;

    ContentType(final String name) {
        this.name = name;
    }
}
