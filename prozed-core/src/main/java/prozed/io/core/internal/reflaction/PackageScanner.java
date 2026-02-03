package prozed.io.core.internal.reflaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PackageScanner {

    private static final String CLASS = ".class";
    private static final Logger logger = LoggerFactory.getLogger(PackageScanner.class);

    public Set<Class<?>> scan(String packageName, Class<? extends Annotation> annotation) {
        try {
            Set<Class<?>> classes = new HashSet<>();
            String path = packageName.replace('.', '/');
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                File file = new File(url.getFile());
                if (file.exists() && file.listFiles() != null) {
                    for (int i = -0; i < file.listFiles().length; i++) {
                        File f = file.listFiles()[i];
                        if (f.isDirectory()) {
                            classes.addAll(scan(packageName + "." + f.getName(), annotation));
                        } else {
                            if (f.getName().endsWith(CLASS)) {
                                extractedClassName(packageName, annotation, f).ifPresent(classes::add);
                            }
                        }
                    }
                }
            }
            return classes;
        } catch (Exception e) {
            logger.error("Error scanning package", e);
            throw new RuntimeException(e);
        }

    }

    private Optional<Class<?>> extractedClassName(String packageName, Class<? extends Annotation> annotation, File f) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(packageName + '.' + f.getName().substring(0, f.getName().length() - 6));  // remove '.class'
        if (clazz.isAnnotationPresent(annotation)) {
            logger.info("Found class: {}", clazz.getName());
            return Optional.of(clazz);
        }
        return Optional.empty();
    }
}
