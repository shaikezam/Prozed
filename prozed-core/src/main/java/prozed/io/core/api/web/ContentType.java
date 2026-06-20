package prozed.io.core.api.web;

public enum ContentType {
    APPLICATION_JSON("application/json;charset=UTF-8"),
    TEXT_PLAIN("text/plain;charset=UTF-8");

    private final String name;

    ContentType(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }
}
