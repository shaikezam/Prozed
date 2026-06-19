package prozed.io.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.jdbc.exception.JdbcOperationsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static prozed.io.jdbc.utils.Constants.*;

@Bean
public class JdbcOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOperations.class);
    private final DataSource pool;
    private final ThreadLocal<Connection> txConnection = new ThreadLocal<>();

    public JdbcOperations() {
        pool = buildDataSource();
    }

    public javax.sql.DataSource getDataSource() {
        return pool;
    }

    public <T> T inTransaction(JdbcCallback<T> work) {
        if (txConnection.get() != null) {
            try {
                return work.run(txConnection.get());
            } catch (SQLException e) {
                throw new JdbcOperationsException("Transaction work failed", e);
            }
        }
        Connection conn = null;
        try {
            conn = pool.getConnection();
            conn.setAutoCommit(false);
            txConnection.set(conn);
            T result = work.run(conn);
            conn.commit();
            return result;
        } catch (Exception e) {
            rollback(conn);
            throw new JdbcOperationsException("Transaction roll back", e);
        } finally {
            txConnection.remove();
            restoreAutoCommitAndClose(conn);
        }
    }

    public <T> T execute(String sql, ResultSetHandler<T> handler, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = borrow();
            preparedStatement = connection.prepareStatement(sql);
            bindParams(preparedStatement, params);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return handler.handle(rs);
            }
        } catch (SQLException e) {
            throw new JdbcOperationsException("Failed to execute query: %s".formatted(sql), e);
        } finally {
            closeQuietly(preparedStatement);
            release(connection);
        }
    }

    public int update(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = borrow();
            preparedStatement = connection.prepareStatement(sql);
            bindParams(preparedStatement, params);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcOperationsException("Failed to execute update: %s".formatted(sql), e);
        } finally {
            closeQuietly(preparedStatement);
            release(connection);
        }
    }

    public <T> List<T> select(String sql, RowMapper<T> mapper, Object... params) {
        return execute(sql, rs -> {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            return results;
        }, params);
    }

    public <T> T selectOne(String sql, RowMapper<T> mapper, Object... params) {
        return execute(sql, rs -> {
            if (rs.next()) {
                return mapper.map(rs);
            }
            return null;
        }, params);
    }

    public void preDestroy() {
        LOGGER.info("Destroying JdbcOperations");
        if (this.pool != null) {
            this.pool.close();
        }
    }

    protected Connection borrow() throws SQLException {
        Connection conn = txConnection.get();
        return (conn != null) ? conn : pool.getConnection();
    }

    protected void release(Connection conn) {
        if (conn != null && conn != txConnection.get()) {
            closeQuietly(conn);
        }
    }

    private void restoreAutoCommitAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // best effort
            }
        }
        closeQuietly(conn);
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                // best effort
            }
        }
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static DataSource buildDataSource() {
        DataSource ds = new DataSource();

        ds.setUrl(ProzedPropertiesWrapper.getProperty(DB_URL));
        ds.setDriverClassName(ProzedPropertiesWrapper.getProperty(DB_DRIVER_CLASS_NAME));
        ds.setUsername(ProzedPropertiesWrapper.getProperty(DB_USERNAME));
        ds.setPassword(ProzedPropertiesWrapper.getProperty(DB_PASSWORD));

        ds.setInitialSize(parseInt(DB_POOL_INITIAL_SIZE, "2"));
        ds.setMinIdle(parseInt(DB_POOL_MIN_IDLE, "2"));
        ds.setMaxIdle(parseInt(DB_POOL_MAX_IDLE, "5"));
        ds.setMaxActive(parseInt(DB_POOL_MAX_ACTIVE, "20"));

        ds.setMaxWait(parseInt(DB_POOL_MAX_WAIT, "10000"));

        ds.setTestOnBorrow(parseBool(DB_POOL_TEST_ON_BORROW, "true"));
        ds.setValidationQuery(ProzedPropertiesWrapper.getProperty(DB_POOL_VALIDATION_QUERY, "SELECT 1"));
        ds.setValidationInterval(parseInt(DB_POOL_VALIDATION_INTERVAL, "30000"));

        ds.setRemoveAbandoned(parseBool(DB_POOL_REMOVE_ABANDONED, "true"));
        ds.setRemoveAbandonedTimeout(parseInt(DB_POOL_REMOVE_ABANDONED_TIMEOUT, "60"));
        ds.setLogAbandoned(parseBool(DB_POOL_LOG_ABANDONED, "true"));

        return ds;
    }

    private static int parseInt(String key, String defaultValue) {
        return Integer.parseInt(ProzedPropertiesWrapper.getProperty(key, defaultValue));
    }

    private static boolean parseBool(String key, String defaultValue) {
        return Boolean.parseBoolean(ProzedPropertiesWrapper.getProperty(key, defaultValue));
    }
}