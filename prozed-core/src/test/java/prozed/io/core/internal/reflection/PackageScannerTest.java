package prozed.io.core.internal.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.di.Bean;
import org.junit.jupiter.api.io.TempDir;
import prozed.io.core.internal.reflection.test.AnnotatedClass;
import prozed.io.core.internal.scanjar.JarBean;
import prozed.io.core.internal.scanjarx.SiblingJarBean;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PackageScannerTest {

    @InjectMocks
    private PackageScanner scanner;

    @Test
    void testScan() {
        // given
        Set<Class<?>> actual = Set.of(
                FirstAnnotatedClass.class,
                SecondAnnotatedClass.class,
                AnnotatedClass.class
        );

        // when
        Set<Class<?>> excepted = scanner.scan(
                "prozed.io.core.internal.reflection",
                Bean.class);

        // then
        assertEquals(actual, excepted);
    }

    @Test
    void testScanJarExcludesPrefixSiblingPackages(@TempDir Path tempDir) throws Exception {
        // given
        Path jar = tempDir.resolve("scan-test.jar");
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jar))) {
            jos.putNextEntry(new JarEntry("prozed/io/core/internal/scanjar/")); // dir entry so getResources finds the jar
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("prozed/io/core/internal/scanjar/JarBean.class"));
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("prozed/io/core/internal/scanjarx/SiblingJarBean.class"));
            jos.closeEntry();
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, original)) {
            Thread.currentThread().setContextClassLoader(loader);

            // when
            Set<Class<?>> result = scanner.scan("prozed.io.core.internal.scanjar", Bean.class);

            // then
            assertTrue(result.contains(JarBean.class));
            assertFalse(result.contains(SiblingJarBean.class));
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Bean
    public static class FirstAnnotatedClass {
    }

    @Bean
    public static class SecondAnnotatedClass {
    }

    public static class NoneAnnotatedClass {
    }

}