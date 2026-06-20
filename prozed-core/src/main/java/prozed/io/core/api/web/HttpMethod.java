package prozed.io.core.api.web;

import java.util.Arrays;
import java.util.List;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    private static final List<HttpMethod> REQUIRED_PAYLOADS = Arrays.asList(POST, PUT);

    public static HttpMethod fromString(String name) {
        return valueOf(name.toUpperCase());
    }

    public static boolean requirePayload(HttpMethod httpMethod) {
        return REQUIRED_PAYLOADS.contains(httpMethod);
    }
}
