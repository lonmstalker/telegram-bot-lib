package io.lonmstalker.tgkit.core.loader;

import io.lonmstalker.tgkit.core.BotCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * Фабрика, позволяющая расширять {@link BotCommand}.
 *
 * @param <A> тип аннотации, по которой срабатывает фабрика
 */
public interface BotCommandFactory<A extends Annotation> {

    /**
     * @return класс аннотации, по которой нужно применить этот фабричный алгоритм(null == любая)
     */
    @SuppressWarnings("unchecked")
    default @NonNull Class<A> annotationType() {
        return (Class<A>) None.class;
    }

    /**
     * Вызывается при обнаружении аннотации {@linkplain #annotationType()}
     * на методе-хендлере. Доступна команда и сам метод.
     *
     * @param command команда {@link BotCommand}
     * @param method  метод-хендлер
     * @param ann     экземпляр аннотации
     */
    void apply(@NonNull BotCommand<?> command,
               @NonNull Method method,
               @Nullable A ann);

    /**
     * BotCommandFactory применяется на все команды
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface None {
    }

}
