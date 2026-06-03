package prozed.io.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface JdbcCallback<T> {
    T run(Connection connection) throws SQLException;
}
