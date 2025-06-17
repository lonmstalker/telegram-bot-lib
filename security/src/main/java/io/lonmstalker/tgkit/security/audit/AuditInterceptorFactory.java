package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptorFactory;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class AuditInterceptorFactory implements BotInterceptorFactory<Audit> {

    private static final Map<Method, BotInterceptor> CACHE = new ConcurrentHashMap<>();

    @Override
    public @NonNull Class<Audit> annotationType() {
        return Audit.class;
    }

    @Override
    public @NonNull Optional<BotInterceptor> build(@NonNull Method m, @NonNull Audit _unused) {
        return Optional.of(CACHE.computeIfAbsent(m, this::create));
    }

    private @NonNull BotInterceptor create(@NonNull Method method) {
        AuditConverter conv;
        Audit custom = findAnnotation(method);

        if (custom.converter() != null) {
            conv = newInstance(custom.converter());
        } else {
            EnumSet<AuditField> set = EnumSet.copyOf(List.of(custom.value()));
            conv = new UpdateAuditConverter(set); // дефолт
        }
        return new DelegatingAuditInterceptor(BotSecurityGlobalConfig.INSTANCE.audit().bus(), conv);
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<T> type) {
        try {
            var ctor = MethodHandles.lookup()
                    .findConstructor(type, MethodType.methodType(void.class))
                    .asType(MethodType.methodType(Object.class));
            return (T) ctor.invokeExact();
        } catch (Throwable t) {
            throw new IllegalStateException("Cannot instantiate " + type.getName(), t);
        }
    }

    private static Audit findAnnotation(@NonNull Method m) {
        return Optional.ofNullable(m.getAnnotation(Audit.class))
                .orElse(m.getDeclaringClass().getAnnotation(Audit.class));
    }
}
