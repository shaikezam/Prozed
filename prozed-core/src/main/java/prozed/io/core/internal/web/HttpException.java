package prozed.io.core.internal.web;

public class HttpException extends RuntimeException {
    private final HttpCode httpCode;

    public HttpException(String message, HttpCode httpCode) {
        super(message);
        this.httpCode = httpCode;
    }
}
