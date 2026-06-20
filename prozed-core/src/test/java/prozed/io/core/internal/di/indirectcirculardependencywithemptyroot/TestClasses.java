package prozed.io.core.internal.di.indirectcirculardependencywithemptyroot;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;

public class TestClasses {
    @Bean
    public static class BeanClassA {
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
