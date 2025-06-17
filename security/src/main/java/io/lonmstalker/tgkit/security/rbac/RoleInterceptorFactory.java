package io.lonmstalker.tgkit.security.rbac;

import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoleInterceptorFactory
        implements BotInterceptorFactory<RequiresRole> {

    @Override public @NonNull Class<RequiresRole> annotationType() { return RequiresRole.class; }

    @Override
    public @NonNull Optional<BotInterceptor> build(@NonNull Method m,
                                                   @NonNull RequiresRole ignored) {

        // метод + класс (повторяемая аннотация)
        var anns = Arrays.stream(m.getAnnotationsByType(RequiresRole.class))
                .collect(Collectors.toSet());
        anns.addAll(Arrays.asList(m.getDeclaringClass()
                .getAnnotationsByType(RequiresRole.class)));

        if (anns.isEmpty()) return Optional.empty();

        var roles = anns.stream()
                .flatMap(a -> Arrays.stream(a.value()))
                .collect(Collectors.toSet());

        return Optional.of(new RoleInterceptor(roles));
    }
}
