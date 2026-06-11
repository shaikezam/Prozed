package prozed.io.core.internal.di;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.core.internal.reflection.PackageScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ProzedContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProzedContainer.class);
    private final Map<Class<?>, Object> beansMapping = new ConcurrentHashMap<>();
    private final Set<Class<?>> injectedClasses = new HashSet<>();
    private final Set<Class<?>> processed = new HashSet<>();
    private final Set<Class<?>> visiting = new HashSet<>();
    private final Set<Class<?>> beanedClasses = new HashSet<>();
    private final PackageScanner packageScanner = new PackageScanner();

    public void init(final String baseApplicationPath) {
        loadModulesBeans();
        findBeansAndInjectedClasses(baseApplicationPath);
        validateAllInjectedAreBeans();
        buildDependencyTree();
        for (Map.Entry<Class<?>, Object> entry : beansMapping.entrySet()) {
            try {
                Method init = entry.getKey().getDeclaredMethod("postInit");
                init.invoke(entry.getValue());
            } catch (NoSuchMethodException ignored) {
                // no postInit method, that's fine
            } catch (Exception e) {
                throw new RuntimeException("Prozed: Failed to call postInit() on " + entry.getKey().getName(), e);
            }
        }
    }

    public Object get(Class<?> clazz) {
        return beansMapping.get(clazz);
    }

    public void registerBean(Class<?> clazz, Object bean) {
        beansMapping.put(clazz, bean);
    }

    private void findBeansAndInjectedClasses(String baseApplicationPath) {
        Set<Class<?>> scanned = packageScanner.scan(baseApplicationPath, Bean.class);
        beanedClasses.addAll(scanned); // ✅ merge, keep module beans
        for (Class<?> clazz : beanedClasses) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    injectedClasses.add(field.getType());
                }
            }
        }
    }

    private void validateAllInjectedAreBeans() {
        List<Class<?>> notMarkAsBeans = injectedClasses
                .stream()
                .filter(c -> !beanedClasses.contains(c))
                .toList();
        if (notMarkAsBeans.isEmpty()) {
            return;
        }
        String errorMessage = "Can't inject [%s], fields are not marked as @Bean".formatted(notMarkAsBeans);
        LOGGER.error(errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    private void buildDependencyTree() {
        if (beanedClasses.containsAll(injectedClasses) && injectedClasses.containsAll(beanedClasses)) {
            throw new IllegalStateException("Cycle detected");
        }
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
            String errorMessage = "Cycle detected: %s".formatted(clazz.getName());
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
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
        try {
            Method init = clazz.getDeclaredMethod("preInit");
            init.invoke(currentInstance);
        } catch (NoSuchMethodException ignored) {
            // no preInit method, that's fine
        } catch (Exception e) {
            throw new RuntimeException("Prozed: Failed to call preInit() on " + clazz.getName(), e);
        }

        visiting.remove(clazz);
        processed.add(clazz);
    }

    private void loadModulesBeans() {
        try {
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources("META-INF/services/prozed.io.core.api.di.Bean");

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    reader.lines()
                            .map(String::trim)
                            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                            .forEach(className -> {
                                try {
                                    Class<?> clazz = Class.forName(className);
                                    if (!clazz.isAnnotationPresent(Bean.class)) {
                                        throw new RuntimeException("Prozed: " + className + " not marked as Bean");
                                    }
                                    beanedClasses.add(clazz);
                                    LOGGER.info("Prozed: registered module bean {}", className);
                                } catch (ClassNotFoundException e) {
                                    throw new IllegalStateException("Prozed: module bean not found: %s".formatted(className), e);
                                }
                            });
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Prozed: failed to load module beans", e);
        }
    }
}