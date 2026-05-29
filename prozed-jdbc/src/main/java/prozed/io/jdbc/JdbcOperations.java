package prozed.io.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import prozed.io.core.api.di.Bean;
import prozed.io.core.internal.properties.ProzedPropertiesWrapper;
import prozed.io.jdbc.exception.JdbcOperationsException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static prozed.io.jdbc.utils.Constants.*;

@Bean
public class JdbcOperations {

    private static final DataSource POOL;

    static {
        POOL = buildDataSource();
    }

    // ── Core Execute ──────────────────────────────────────────────────────────

    public <T> T execute(String sql, ResultSetHandler<T> handler, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = POOL.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            bindParams(preparedStatement, params);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return handler.handle(rs);
            }
        } catch (SQLException e) {
            throw new JdbcOperationsException("Failed to execute query: %s".formatted(sql), e);
        } finally {
            closeQuietly(preparedStatement);
            closeQuietly(connection);
        }
    }

    // ── DML ───────────────────────────────────────────────────────────────────

    public int update(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = POOL.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            bindParams(preparedStatement, params);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcOperationsException("Failed to execute update: %s".formatted(sql), e);
        } finally {
            closeQuietly(preparedStatement);
            closeQuietly(connection);
        }
    }

    public int insert(String sql, Object... params) {
        return update(sql, params);
    }

    public int delete(String sql, Object... params) {
        return update(sql, params);
    }

    // ── Query ─────────────────────────────────────────────────────────────────

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

    public <T> Stream<T> stream(String sql, RowMapper<T> mapper, Object... params) {
        return select(sql, mapper, params).stream();
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public void close() {
        POOL.close();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    // ── Pool Setup ────────────────────────────────────────────────────────────

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