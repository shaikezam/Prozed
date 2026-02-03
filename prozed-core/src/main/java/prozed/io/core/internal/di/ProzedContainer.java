package prozed.io.core.internal.di;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.internal.reflaction.PackageScanner;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ProzedContainer {

    private static final Logger logger = LoggerFactory.getLogger(ProzedContainer.class);
    private final Map<Class<?>, Object> beansMapping = new ConcurrentHashMap<>();
    private final Set<Class<?>> injectedClasses = new HashSet<>();
    private final Set<Class<?>> processed = new HashSet<>();
    private final Set<Class<?>> visiting = new HashSet<>();
    private Set<Class<?>> beanedClasses = new HashSet<>();
    private final PackageScanner packageScanner = new PackageScanner();

    public ProzedContainer(final String baseApplicationPath) {
        try {
            findBeansAndInjectedClasses(baseApplicationPath);
            validateAllInjectedAreBeans();
            buildDependencyTree();
        } catch (Exception e) {
            beansMapping.clear();
            injectedClasses.clear();
            throw new RuntimeException(e);
        }
    }

    public Object get(Class<?> clazz) {
        return beansMapping.get(clazz);
    }

    private void findBeansAndInjectedClasses(String baseApplicationPath) {
        try {
            this.beanedClasses = packageScanner.scan(baseApplicationPath, Bean.class);
            for (Class<?> clazz : beanedClasses) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Inject.class)) {
                        injectedClasses.add(field.getType());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateAllInjectedAreBeans() {
        List<Class<?>> notMarkAsBeans = injectedClasses
                .stream()
                .filter(c -> !beanedClasses.contains(c))
                .toList();
        String errorMessage = "Can't inject [%s}, fields are not marked as @Bean".formatted(notMarkAsBeans);
        logger.error(errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    private void buildDependencyTree() {
        List<Class<?>> allRootBeans = beanedClasses
                .stream()
                .filter(c -> !injectedClasses.contains(c))
                .toList();
        for (Class<?> root : allRootBeans) {
            if (!processed.contains(root)) {
                buildTree(root);
            }
        }
    }

    private void buildTree(Class<?> clazz) {
        if (visiting.contains(clazz)) {
            throw new IllegalStateException("Cycle detected: " + clazz.getName());
        }
        if (processed.contains(clazz)) return;
        visiting.add(clazz);
        Object currentInstance = beansMapping.computeIfAbsent(clazz, c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Prozed: Failed to create " + c.getName(), e);
            }
        });
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldClazz = field.getType();
                buildTree(fieldClazz);
                field.setAccessible(true);
                try {
                    field.set(currentInstance, beansMapping.get(fieldClazz));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        visiting.remove(clazz);
        processed.add(clazz);
    }
}