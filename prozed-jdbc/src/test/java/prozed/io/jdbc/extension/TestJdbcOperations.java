package prozed.io.jdbc.extension;

import prozed.io.jdbc.JdbcOperations;

import java.sql.Connection;
import java.sql.SQLException;

public class TestJdbcOperations extends JdbcOperations {

    // Volatile field ensures visibility across both JUnit runner thread and Tomcat server threads
    private volatile Connection testConnection = null;

    /**
     * Injects or clears the shared uncommitted connection for the active test case.
     */
    public void setTestConnection(Connection conn) {
        this.testConnection = conn;
    }

    /**
     * Intercepts the query lifecycle. If a test connection is bound to this class,
     * it bypasses ThreadLocal variables and hands out the test connection instead.
     */
    @Override
    protected Connection borrow() throws SQLException {
        if (testConnection != null) {
            return testConnection;
        }
        return super.borrow();
    }

    /**
     * Prevents internal operations from accidentally closing the shared connection
     * midway through an HTTP request lifecycle.
     */
    @Override
    protected void release(Connection conn) {
        if (conn != null && conn == testConnection) {
            // Do nothing! Let the ProzedTestExtension close it at the end of the test.
            return;
        }
        super.release(conn);
    }
}
