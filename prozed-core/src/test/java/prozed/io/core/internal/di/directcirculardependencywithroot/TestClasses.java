package prozed.io.core.internal.di.directcirculardependencywithroot;

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
        private BeanClassC beanClass;
    }

    @Bean
    public static class BeanClassC {
        @Inject
        private BeanClassB beanClass;
    }
}
