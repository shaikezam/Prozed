package prozed.io.core.internal.di.directcirculardependency;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;

public class TestClasses {
    @Bean
    public static class BeanClassA {
        @Inject
        private BeanClassB beanClass;
    }

    @Bean
    public static class BeanClassB {
        @Inject
        private BeanClassA beanClass;
    }
}