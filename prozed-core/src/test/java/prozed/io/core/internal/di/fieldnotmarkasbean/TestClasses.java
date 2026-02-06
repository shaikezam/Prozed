package prozed.io.core.internal.di.fieldnotmarkasbean;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;

public class TestClasses {
    @Bean
    public static class BeanClass {
        @Inject
        private EmptyClass beanClass;
    }

    public static class EmptyClass {

    }
}
