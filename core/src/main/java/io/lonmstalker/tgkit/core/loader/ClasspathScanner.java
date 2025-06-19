package io.lonmstalker.tgkit.core.loader;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Utility for scanning the classpath for annotated classes.
 */
public final class ClasspathScanner {
    private ClasspathScanner() {}

    /**
     * Finds all classes annotated with the given annotation under the package.
     */
    public static @NonNull Set<Class<?>> findAnnotated(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String basePackage) {
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .forPackages(basePackage)
                .addScanners(Scanners.TypesAnnotated);
        Reflections reflections = new Reflections(cb);
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
