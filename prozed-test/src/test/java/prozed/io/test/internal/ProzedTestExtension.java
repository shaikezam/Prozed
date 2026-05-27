package prozed.io.test.internal;

import org.junit.jupiter.api.extension.*;
import prozed.io.test.api.ProzedTest;

import java.lang.reflect.Method;

public class ProzedTestExtension implements BeforeAllCallback, AfterAllCallback {

    private static final String SERVER_THREAD_KEY = "prozed.server.thread";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ProzedTest annotation = context.getRequiredTestClass().getAnnotation(ProzedTest.class);

        if (annotation.mainClass() == Void.class) {
            throw new IllegalStateException("mainClass must be specified in @ProzedTest");
        }

        Class<?> mainClass = annotation.mainClass();
        Method mainMethod = mainClass.getMethod("main", String[].class);

        // Start main method in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) annotation.mainArgs());
            } catch (Exception e) {
                throw new RuntimeException("Failed to start main method", e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server to start
        Thread.sleep(2000);

        // Store thread reference for cleanup
        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(SERVER_THREAD_KEY, serverThread);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Thread serverThread = (Thread) context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(SERVER_THREAD_KEY);

        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }
}