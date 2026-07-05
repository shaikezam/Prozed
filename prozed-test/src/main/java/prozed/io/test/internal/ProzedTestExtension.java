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
    private static final String SCHEDULER_CONTAINER_CLASS = "prozed.io.core.api.scheduling.SchedulerContainer";
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
        }

        if (annotation.mainClass() == Void.class) {
            throw new IllegalStateException("mainClass must be specified in @ProzedTest");
        }

        Class<?> mainClass = annotation.mainClass();
        Method mainMethod = mainClass.getMethod("main", String[].class);
        Thread serverThread = null;
        try {
            serverThread = new Thread(() -> {
                try {
                    mainMethod.invoke(null, (Object) annotation.mainArgs());
                } catch (Exception e) {
                    LOGGER.error("Failed to start main method", e);
                    Thread.currentThread().interrupt();
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();

            waitForServer(Integer.parseInt(TestPropertiesReader.getProperty("web.service.port")));
        } catch (Exception e) {
            LOGGER.error("Failed to start Prozed server in test", e);
            throw e;
        }

        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(SERVER_THREAD_KEY, serverThread);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (this.isCleanUpSupport) {
            invokeScheduler("pause");

            Class<?> jdbcOpertaionsClass = Class.forName(JDBC_OPS_CLASS);
            Method getDataSourceMethod = jdbcOpertaionsClass.getMethod("getDataSource");
            javax.sql.DataSource dataSource = (javax.sql.DataSource) getDataSourceMethod.invoke(testJdbcOpsInstance);
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Method setTestConnectionMethod = Class.forName(TEST_JDBC_OPS_CLASS).getMethod("setTestConnection", Connection.class);
            setTestConnectionMethod.invoke(testJdbcOpsInstance, conn);
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(TEST_CONN_KEY, conn);
            invokeScheduler("resume");
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (this.isCleanUpSupport) {
            invokeScheduler("pause");

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

    private void invokeScheduler(String method) {
        try {
            Class<?> containerClass = Class.forName(CONTAINER_CLASS);
            Class<?> schedulerClass = Class.forName(SCHEDULER_CONTAINER_CLASS);
            Object scheduler = containerClass.getMethod("get", Class.class).invoke(containerInstance, schedulerClass);
            if (scheduler != null) {
                schedulerClass.getMethod(method).invoke(scheduler);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to {} scheduler during cleanUp", method, e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Thread serverThread = (Thread) context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(SERVER_THREAD_KEY);

        try {
            Class<?> prozedServerClass = Class.forName(SERVER_CLASS);
            prozedServerClass.getMethod("shutdownCurrent").invoke(null);
            interrupt(serverThread);
            if (serverThread != null) {
                serverThread.join(10000);
            }
            prozedServerClass.getMethod("resetContainer").invoke(null);
        } catch (Exception e) {
            LOGGER.warn("Failed to shut down ProzedServer", e);
        }
    }

    private static void interrupt(Thread serverThread) {
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
