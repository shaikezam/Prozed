package prozed.io.core.internal.di;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.internal.di.container.TestClasses;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProzedContainerTest {

    @Test
    void testFindBeansAndInjectedClassesThrowsException() {
        // given & when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                    new ProzedContainer("prozed.io.core.internal.di.fieldnotmarkasbean");
                }
        );
        assertTrue(exception.getMessage().contains("not marked as @Bean"));
    }

    @Test
    void testDirectCircularDependencyThrowsException() {
        // given & when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                    new ProzedContainer("prozed.io.core.internal.di.directcirculardependency");
                }
        );
        assertTrue(exception.getMessage().contains("Cycle detected"));
    }

    @Test
    void testDirectCircularDependencyWithRootThrowsException() {
        // given & when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                    new ProzedContainer("prozed.io.core.internal.di.directcirculardependencywithroot");
                }
        );
        assertTrue(exception.getMessage().contains("Cycle detected"));
    }

    @Test
    void testInDirectCircularDependencyWithRootThrowsException() {
        // given & when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                    new ProzedContainer("prozed.io.core.internal.di.indirectcirculardependencywithroot");
                }
        );
        assertTrue(exception.getMessage().contains("Cycle detected"));
    }

    @Test
    void testContainerResolvesValidDependencyChain() {
        // given & when
        ProzedContainer container = new ProzedContainer("prozed.io.core.internal.di.container");

        // then
        assertNotNull(container.get(TestClasses.BeanClassA1.class));
        assertNotNull(container.get(TestClasses.BeanClassA2.class));
        assertNotNull(container.get(TestClasses.BeanClassB.class));
        assertNotNull(container.get(TestClasses.BeanClassC.class));
        assertNotNull(container.get(TestClasses.BeanClassD.class));
        assertNotNull(container.get(TestClasses.BeanClassE.class));
        assertSame(container.get(TestClasses.BeanClassE.class), container.get(TestClasses.BeanClassE.class));
    }

}