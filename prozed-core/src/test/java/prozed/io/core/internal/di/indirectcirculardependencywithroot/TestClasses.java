package prozed.io.core.internal.di.indirectcirculardependencywithroot;

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
        private BeanClassD beanClass;
    }

    @Bean
    public static class BeanClassD {
        @Inject
        private BeanClassB beanClass;
    }
}
