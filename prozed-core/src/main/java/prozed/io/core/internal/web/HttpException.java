package prozed.io.core.internal.web;

import prozed.io.core.api.web.HttpCode;

public class HttpException extends RuntimeException {
    private final HttpCode httpCode;

    public HttpException(String message, HttpCode httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public HttpException(String message, HttpCode httpCode, Throwable e) {
        super(message, e);
        this.httpCode = httpCode;
    }

    public HttpCode getHttpCode() {
        return httpCode;
    }
}
