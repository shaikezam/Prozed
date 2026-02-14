package prozed.io.core.api.web;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod fromString(String name) {
        return valueOf(name.toUpperCase());
    }
}
