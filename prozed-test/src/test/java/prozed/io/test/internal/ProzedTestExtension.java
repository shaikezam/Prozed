package prozed.io.test.internal;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.utils.TestPropertiesReader;

import java.lang.reflect.Method;
import java.sql.Connection;

public class ProzedTestExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private static final String SERVER_THREAD_KEY = "prozed.server.thread";
    private static final String TEST_CONN_KEY = "active.test.connection";
    private static final String TEST_JDBC_OPS_CLASS = "prozed.io.jdbc.extension.TestJdbcOperations";
    private static final String JDBC_OPS_CLASS = "prozed.io.jdbc.JdbcOperations";
    private static final String SERVER_CLASS = "prozed.io.core.api.web.ProzedServer";
    private static final String CONTAINER_CLASS = "prozed.io.core.internal.di.ProzedContainer";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProzedTestExtension.class);

    private boolean isCleanUpSupport = false;
    private Object containerInstance = null;
    private Object testJdbcOpsInstance = null;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ProzedTest annotation = context.getRequiredTestClass().getAnnotation(ProzedTest.class);
        this.isCleanUpSupport = isJdbcOperationsTestPresent() && annotation.cleanUp();
        if (this.isCleanUpSupport) {
            LOGGER.info("Prozed: cleanup before test extension");
            Class<?> prozedServerClass = Class.forName(SERVER_CLASS);
            Class<?> prozedContainerClass = Class.forName(CONTAINER_CLASS);
            Method getContainerMethod = prozedServerClass.getMethod("getContainer");
            this.containerInstance = getContainerMethod.invoke(null);
            Class<?> testJdbcOpsCls = Class.forName(TEST_JDBC_OPS_CLASS);
            this.testJdbcOpsInstance = testJdbcOpsCls.getConstructor().newInstance();
            Method registerMethod = prozedContainerClass.getMethod("registerBean", Class.class, Object.class);
            registerMethod.invoke(this.containerInstance, Class.forName(JDBC_OPS_CLASS), this.testJdbcOpsInstance);
            int i = 1;
            // inject TestJdbcOperations to ProzedContainer
            // AfterEacgh rollback conenction
        }

        if (annotation.mainClass() == Void.class) {
            throw new IllegalStateException("mainClass must be specified in @ProzedTest");
        }

        Class<?> mainClass = annotation.mainClass();
        Method mainMethod = mainClass.getMethod("main", String[].class);

        Thread serverThread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) annotation.mainArgs());
            } catch (Exception e) {
                throw new RuntimeException("Failed to start main method", e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        waitForServer(Integer.parseInt(TestPropertiesReader.getProperty("web.service.port")));
        Thread.sleep(2000);

        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(SERVER_THREAD_KEY, serverThread);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (this.isCleanUpSupport) {
            Class<?> jdbcOpertaionsClass = Class.forName(JDBC_OPS_CLASS);
            Method getDataSourceMethod = jdbcOpertaionsClass.getMethod("getDataSource");
            javax.sql.DataSource dataSource = (javax.sql.DataSource) getDataSourceMethod.invoke(testJdbcOpsInstance);
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Method setTestConnectionMethod = Class.forName(TEST_JDBC_OPS_CLASS).getMethod("setTestConnection", Connection.class);
            setTestConnectionMethod.invoke(testJdbcOpsInstance, conn);
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(TEST_CONN_KEY, conn);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (this.isCleanUpSupport) {
            Connection conn = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get(TEST_CONN_KEY, Connection.class);
            if (conn != null && !conn.isClosed()) {
                try {
                    conn.rollback();
                } finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (Exception ignored) {
                    }
                    try {
                        conn.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Thread serverThread = (Thread) context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(SERVER_THREAD_KEY);

        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    private void waitForServer(int port) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 16000;
        while (System.currentTimeMillis() < deadline) {
            try (java.net.Socket socket = new java.net.Socket("localhost", port)) {
                return; // ✅ server is up
            } catch (java.io.IOException e) {
                Thread.sleep(200);
            }
        }
        throw new IllegalStateException("Prozed server did not start within %dms on port %d".formatted(16000, port));
    }

    private boolean isJdbcOperationsTestPresent() {
        try {
            Class.forName(TEST_JDBC_OPS_CLASS, false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
