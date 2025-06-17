package io.lonmstalker.tgkit.security.antispam;

import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Фабрика без аннотаций: регистрирует один и тот же interceptor
 * для всех команд. В ServiceLoader-файле достаточно одной строки.
 */
public final class AntiSpamInterceptorFactory
        implements BotInterceptorFactory<AntiSpamInterceptorFactory.NoAnnotation> {

    private final BotInterceptor delegate;

    public AntiSpamInterceptorFactory(@NonNull BotInterceptor delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NonNull Class<NoAnnotation> annotationType() {
        return NoAnnotation.class;
    }

    @Override
    public @NonNull Optional<BotInterceptor> build(@NonNull Method m, @NonNull NoAnnotation __) {
        return Optional.of(delegate);
    }

    /**
     * “Маркер” для generic-типа, никогда не используется.
     */
    public @interface NoAnnotation {
    }
}
