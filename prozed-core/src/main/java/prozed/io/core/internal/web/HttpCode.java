package prozed.io.core.internal.web;

public enum HttpCode {
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_SUPPORTED(405);
    private final int code;

    HttpCode(int code) {
        this.code = code;
    }
}
