package prozed.io.core.api.exception;


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

    public boolean is5xx() {
        return httpCode >= 500 && httpCode < 600;
    }
}
