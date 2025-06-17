package io.lonmstalker.tgkit.core.interceptor;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public interface BotInterceptorFactory<A extends Annotation> {

    /** Какая аннотация интересует фабрику. */
    @NonNull Class<A> annotationType();

    /**
     *  @return  interceptor, если метод помечен нужной аннотацией.
     */
    @NonNull Optional<BotInterceptor> build(@NonNull Method m, @NonNull A ann);
}