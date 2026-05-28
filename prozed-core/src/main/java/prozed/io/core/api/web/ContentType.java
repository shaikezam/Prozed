package prozed.io.core.api.web;

public enum ContentType {
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain");

    private final String name;

    ContentType(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }
}
