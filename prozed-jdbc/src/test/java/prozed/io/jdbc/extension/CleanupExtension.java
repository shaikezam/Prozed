package prozed.io.jdbc.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.sql.Connection;

public class CleanupExtension implements BeforeEachCallback, AfterEachCallback {

    private static final String TEST_JDBC_OPS_CLASS = "prozed.io.jdbc.extension.TestJdbcOperations";
    private static final String JDBC_OPS_CLASS = "prozed.io.jdbc.JdbcOperations";
    private static final String SERVER_CLASS = "prozed.io.core.api.web.ProzedServer";
    private static final String CONTAINER_CLASS = "prozed.io.core.internal.di.ProzedContainer";

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CleanupExtension.class);
    private static final String TEST_CONN_KEY = "active.test.connection";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!isJdbcExtensionPresent()) return;

        // 1. Fetch the already running container instance from the server
        Class<?> serverCls = Class.forName(SERVER_CLASS);
        Method getContainerMethod = serverCls.getMethod("getContainerInstance");
        Object containerInstance = getContainerMethod.invoke(null);

        Class<?> containerCls = Class.forName(CONTAINER_CLASS);
        Class<?> jdbcOpsCls = Class.forName(JDBC_OPS_CLASS);
        Class<?> testJdbcOpsCls = Class.forName(TEST_JDBC_OPS_CLASS);

        // 2. Get the active bean inside the container
        Method getMethod = containerCls.getMethod("get", Class.class);
        Object currentBeanInstance = getMethod.invoke(containerInstance, jdbcOpsCls);

        Object testJdbcOpsInstance;

        // 3. Verify if the bean inside the container is actually the Test subclass
        if (currentBeanInstance != null && testJdbcOpsCls.isInstance(currentBeanInstance)) {
            // If it's already the test variant (via SPI), reuse it
            testJdbcOpsInstance = currentBeanInstance;
        } else {
            // If it's the production version, create our Test variant right now
            testJdbcOpsInstance = testJdbcOpsCls.getConstructor().newInstance();

            // Force-inject/overwrite the mapping inside the container so controllers use it
            Method registerMethod = containerCls.getMethod("registerBean", Class.class, Object.class);
            registerMethod.invoke(containerInstance, jdbcOpsCls, testJdbcOpsInstance);
        }

        // 4. Open the connection and turn off autocommit
        Method getDataSourceMethod = jdbcOpsCls.getMethod("getDataSource");
        javax.sql.DataSource dataSource = (javax.sql.DataSource) getDataSourceMethod.invoke(testJdbcOpsInstance);

        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);

        // 5. Bind it to the test operation instance safely
        Method setTestConnectionMethod = testJdbcOpsCls.getMethod("setTestConnection", Connection.class);
        setTestConnectionMethod.invoke(testJdbcOpsInstance, conn); // ✅ This will now always succeed

        context.getStore(NAMESPACE).put(TEST_CONN_KEY, conn);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (!isJdbcExtensionPresent()) return;

        Connection conn = context.getStore(NAMESPACE).get(TEST_CONN_KEY, Connection.class);

        // Clear the connection from the active test instance
        Class<?> serverCls = Class.forName(SERVER_CLASS);
        Method getContainerMethod = serverCls.getMethod("getContainerInstance");
        Object containerInstance = getContainerMethod.invoke(null);

        if (containerInstance != null) {
            Class<?> containerCls = Class.forName(CONTAINER_CLASS);
            Class<?> jdbcOpsCls = Class.forName(JDBC_OPS_CLASS);
            Method getMethod = containerCls.getMethod("get", Class.class);
            Object testJdbcOpsInstance = getMethod.invoke(containerInstance, jdbcOpsCls);

            if (testJdbcOpsInstance != null) {
                Class<?> testJdbcOpsCls = Class.forName(TEST_JDBC_OPS_CLASS);
                Method setTestConnectionMethod = testJdbcOpsCls.getMethod("setTestConnection", Connection.class);
                setTestConnectionMethod.invoke(testJdbcOpsInstance, new Object[]{null});
            }
        }

        // Roll everything back completely
        if (conn != null && !conn.isClosed()) {
            try {
                conn.rollback();
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    private boolean isJdbcExtensionPresent() {
        try {
            Class.forName(TEST_JDBC_OPS_CLASS, false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}