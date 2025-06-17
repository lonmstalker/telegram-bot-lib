package io.lonmstalker.tgkit.security.ratelimit;

import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Heavy-weight builder executed once at startup. Converts every
 * {@code @RateLimit} annotation on handler method/class to an immutable
 * {@link RateLimitInterceptor}.
 */
public final class RateLimitInterceptorFactory
        implements BotInterceptorFactory<RateLimit> {

    private final RateLimiter backend;

    public RateLimitInterceptorFactory(@NonNull RateLimiter backend) {
        this.backend = backend;
    }

    @Override
    public @NonNull Class<RateLimit> annotationType() {
        return RateLimit.class;
    }

    @Override
    public @NonNull Optional<BotInterceptor> build(@NonNull Method m,
                                                   @NonNull RateLimit ignored) {
        // 1. Collect all annotations (method + class)
        List<RateLimit> anns = new ArrayList<>();
        anns.addAll(Arrays.asList(m.getAnnotationsByType(RateLimit.class)));
        anns.addAll(Arrays.asList(m.getDeclaringClass().getAnnotationsByType(RateLimit.class)));
        if (anns.isEmpty()) return Optional.empty();

        // 2. Pre-compute stable string prefix “cmd:<method>:”
        String prefix = "cmd:" + m.getName() + ":";

        // 3. Map to immutable meta objects
        List<RateLimitInterceptor.Meta> metas = anns.stream()
                .map(a -> new RateLimitInterceptor.Meta(
                        a.key(), a.permits(), a.seconds(), prefix
                ))
                .toList();

        // 4. Create lightweight interceptor
        return Optional.of(new RateLimitInterceptor(backend, metas));
    }
}
