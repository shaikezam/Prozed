package prozed.io.core.internal.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanner {

    private static final String CLASS = ".class";
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);

    public Set<Class<?>> scan(String packageName, Class<? extends Annotation> annotation) {
        try {
            Set<Class<?>> classes = new HashSet<>();
            String path = packageName.replace('.', '/');
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String decodedUrl = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);

                if (decodedUrl.contains("!")) {
                    // JAR file
                    scanJar(decodedUrl, packageName, annotation, classes);
                } else {
                    // Directory (file system)
                    scanDirectory(decodedUrl, packageName, annotation, classes);
                }
            }
            return classes;
        } catch (Exception e) {
            LOGGER.error("Error scanning package", e);
            throw new RuntimeException(e);
        }
    }

    private void scanDirectory(String dirPath, String packageName, Class<? extends Annotation> annotation, Set<Class<?>> classes) throws ClassNotFoundException {
        LOGGER.info("Scanning directory: {}", dirPath);
        File file = new File(dirPath);
        if (file.exists() && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    scanDirectory(f.getAbsolutePath(), packageName + "." + f.getName(), annotation, classes);
                } else if (f.getName().endsWith(CLASS)) {
                    loadClass(packageName, f.getName(), annotation).ifPresent(classes::add);
                }
            }
        }
    }

    private void scanJar(String jarPath, String packageName, Class<? extends Annotation> annotation, Set<Class<?>> classes) throws Exception {
        LOGGER.info("Scanning jar: {}", jarPath);
        String[] parts = jarPath.split("!");
        String filePath = parts[0].replace("file:", "").replace("\\", "/");

        try (JarFile jarFile = new JarFile(filePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            String packagePath = packageName.replace('.', '/');

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // match entries in this package
                if (entryName.startsWith(packagePath) && entryName.endsWith(CLASS)) {
                    String className = entryName
                            .substring(0, entryName.length() - 6) // remove .class
                            .replace('/', '.');

                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }
    }

    private Optional<Class<?>> loadClass(String packageName, String fileName, Class<? extends Annotation> annotation) throws ClassNotFoundException {
        String className = packageName + '.' + fileName.substring(0, fileName.length() - 6); // remove .class
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(annotation)) {
            return Optional.of(clazz);
        }
        return Optional.empty();
    }
}