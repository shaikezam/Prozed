package prozed.io.test.internal;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import prozed.io.test.api.ProzedTest;
import prozed.io.test.utils.TestPropertiesReader;

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
}
