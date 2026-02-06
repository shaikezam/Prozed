package prozed.io.core.internal.di.container;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;

public class TestClasses {
    @Bean
    public static class BeanClassA1 {
        @Inject
        private BeanClassB beanClass;
    }

    @Bean
    public static class BeanClassA2 {
        @Inject
        private BeanClassE beanClass;
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
        private BeanClassE beanClass;
    }

    @Bean
    public static class BeanClassE {
    }
}
