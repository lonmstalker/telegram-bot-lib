package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.Arg;
import java.lang.reflect.Parameter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Метаданные одного параметра метода:
 *
 * <ul>
 *   <li>{@code order} – порядок переменной в методе;
 *   <li>{@code request} – если {@link BotRequest};
 *   <li>{@code update} – если {@link Update};
 *   <li>{@code arg} – аннотация {@link Arg} /default;
 *   <li>{@code parameter} – параметр в методе {@link Parameter} /default;
 *   <li>{@code converter} – конвертер строки в нужный тип параметра.
 * </ul>
 */
public record ParamInfo(
    int order,
    boolean request,
    boolean update,
    @Nullable Arg arg,
    @NonNull Parameter parameter,
    @NonNull BotArgumentConverter<Object, Object> converter) {}
