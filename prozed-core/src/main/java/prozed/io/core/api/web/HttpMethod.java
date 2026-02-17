package prozed.io.core.api.web;

import java.util.Locale;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String name;

    HttpMethod(final String name) {
        this.name = name;
    }

    public static HttpMethod fromString(final String name) {
        return valueOf(name.toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return "HttpMethod{" +
                "name='" + name + '\'' +
                '}';
    }
}
