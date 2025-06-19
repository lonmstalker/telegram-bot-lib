package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.Arg;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Parameter;

/**
 * Метаданные одного параметра метода:
 * <ul>
 *   <li>{@code order} – порядок переменной в методе;</li>
 *   <li>{@code request} – если {@link BotRequest};</li>
 *   <li>{@code update} – если {@link Update};</li>
 *   <li>{@code arg} – аннотация {@link Arg} /default;</li>
 *   <li>{@code parameter} – параметр в методе {@link Parameter} /default;</li>
 *   <li>{@code converter} – конвертер строки в нужный тип параметра.</li>
 * </ul>
 */
public record ParamInfo(
        int order,
        boolean request,
        boolean update,
        @Nullable Arg arg,
        @NonNull Parameter parameter,
        @NonNull BotArgumentConverter<Object, Object> converter
) {
}