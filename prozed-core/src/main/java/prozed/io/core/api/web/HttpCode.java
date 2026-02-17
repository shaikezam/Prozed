package prozed.io.core.api.web;

import jakarta.servlet.http.HttpServletResponse;

public enum HttpCode {
    NOT_FOUND(404),
    @SuppressWarnings("PMD.LongVariable")
    INTERNAL_SERVER_ERROR(500),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_SUPPORTED(405);
    private final int code;

    HttpCode(final int code) {
        this.code = code;
    }

    public void applyTo(final HttpServletResponse response) {
        response.setStatus(this.code);
    }

}
