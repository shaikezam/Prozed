package prozed.io.core.internal.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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

                if ("jar".equals(url.getProtocol())) {
                    scanJar(url, packageName, annotation, classes);
                } else {
                    String decodedPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    scanDirectory(decodedPath, packageName, annotation, classes);
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
        File[] files = file.listFiles();
        if (file.exists() && files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    scanDirectory(f.getAbsolutePath(), packageName + "." + f.getName(), annotation, classes);
                } else if (f.getName().endsWith(CLASS)) {
                    loadClass(packageName, f.getName(), annotation).ifPresent(classes::add);
                }
            }
        }
    }

    private void scanJar(URL jarUrl, String packageName, Class<? extends Annotation> annotation, Set<Class<?>> classes) throws Exception {
        LOGGER.info("Scanning jar: {}", jarUrl);
        JarURLConnection conn = (JarURLConnection) jarUrl.openConnection();
        try (JarFile jarFile = conn.getJarFile()) {
            String packagePath = packageName.replace('.', '/');
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (isInPackage(entryName, packagePath) && entryName.endsWith(CLASS)) {
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation)) classes.add(clazz);
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        }
    }

    /**
     * Prefix-matches a jar entry to a package. The trailing slash prevents a sibling
     * package sharing a name prefix (e.g. {@code reflection2}) from matching {@code reflection}.
     */
    private boolean isInPackage(String entryName, String packagePath) {
        return entryName.startsWith(packagePath + "/");
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