package io.lonmstalker.tgkit.security.ratelimit;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Heavy-weight builder executed once at startup. Converts every
 * {@code @RateLimit} annotation on handler method/class to an immutable
 * {@link RateLimitInterceptor}.
 */
public final class RateLimitBotCommandFactory implements BotCommandFactory<RateLimit> {

    @Override
    public @NonNull Class<RateLimit> annotationType() {
        return RateLimit.class;
    }

    @Override
    public void apply(@NonNull BotCommand<?> command,
                      @NonNull Method method,
                      @Nullable RateLimit _unused) {

        List<RateLimit> anns = new ArrayList<>();
        anns.addAll(Arrays.asList(method.getAnnotationsByType(RateLimit.class)));
        anns.addAll(Arrays.asList(method.getDeclaringClass().getAnnotationsByType(RateLimit.class)));

        if (anns.isEmpty()) {
            return;
        }

        // 2. Pre-compute stable string prefix “cmd:<method>:”
        String prefix = "cmd:" + method.getName() + ":";

        // 3. Map to immutable meta objects
        List<RateLimitInterceptor.Meta> metas = anns.stream()
                .map(a -> new RateLimitInterceptor.Meta(
                        a.key(), a.permits(), a.seconds(), prefix
                ))
                .toList();

        // 4. Create lightweight interceptor
        command.addInterceptor(new RateLimitInterceptor(
                BotSecurityGlobalConfig.INSTANCE.rateLimit().getBackend(), metas));
    }
}
