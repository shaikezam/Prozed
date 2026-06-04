package prozed.io.core.internal.web;


public class HttpException extends RuntimeException {
    private final int httpCode;

    public HttpException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public HttpException(String message, int httpCode, Throwable e) {
        super(message, e);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
