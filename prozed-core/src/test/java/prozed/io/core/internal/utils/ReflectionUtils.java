package prozed.io.core.internal.utils;

import java.lang.reflect.Method;

public class ReflectionUtils {
    public static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
