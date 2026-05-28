package prozed.io.test.api;

import org.junit.jupiter.api.extension.ExtendWith;
import prozed.io.test.internal.ProzedTestExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ProzedTestExtension.class)
public @interface ProzedTest {
    Class<?> mainClass() default Void.class;
    String[] mainArgs() default {};
}
