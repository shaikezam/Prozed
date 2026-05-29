package prozed.io.jdbc.exception;

public class JdbcOperationsException extends RuntimeException {
    public JdbcOperationsException(String message, Throwable cause) {
        super(message, cause);
    }
}
