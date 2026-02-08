package prozed.io.core.internal.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.di.Bean;
import prozed.io.core.internal.reflection.PackageScanner;
import prozed.io.core.internal.reflection.test.AnnotatedClass;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PackageScannerTest {

    @InjectMocks
    private PackageScanner scanner;

    @Test
    void testScan() {
        // given
        //PackageScanner scanner = new PackageScanner();
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

    @Bean
    public static class FirstAnnotatedClass {
    }

    @Bean
    public static class SecondAnnotatedClass {
    }

    public static class NoneAnnotatedClass {
    }

}