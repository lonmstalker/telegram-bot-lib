package io.lonmstalker.tgkit.core.loader;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotHandlerConverter;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.args.BotArgumentConverter;
import io.lonmstalker.tgkit.core.args.Context;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import lombok.AccessLevel;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Builder(access = AccessLevel.PACKAGE)
class InternalCommandAdapter implements BotCommand<BotApiObject> {
    private final int order;
    private final @NonNull Method method;
    private final @NonNull Object instance;
    private final @NonNull String botGroup;
    private final @NonNull BotRequestType type;
    private final @NonNull BotHandlerConverter<?> converter;
    private final @NonNull CommandMatch<? extends BotApiObject> commandMatch;
    private final ParamInfo[] params;

    @Override
    @SuppressWarnings("argument")
    public @Nullable BotResponse handle(@NonNull BotRequest<BotApiObject> request) {
        try {
            Object converted = converter.convert(request);
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                ParamInfo pi = params[i];
                if (pi.request) {
                    args[i] = converted;
                } else {
                    String raw = null;
                    var matcher = RouteContextHolder.getMatcher();
                    if (matcher != null) {
                        try {
                            raw = matcher.group(pi.arg.value());
                        } catch (IllegalArgumentException ignored) {
                            // group not found
                        }
                    }
                    if ((raw == null || raw.isEmpty()) && !pi.arg.required()) {
                        raw = pi.arg.defaultValue();
                    }
                    if (raw == null || raw.isEmpty()) {
                        throw new BotApiException("Required arg missing: " + pi.arg.value());
                    }
                    Context ctx = new Context(request, matcher);
                    args[i] = Objects.requireNonNull(pi.converter.convert(raw, ctx));
                }
            }

            Object res = method.invoke(instance, args);
            if (res == null) {
                return null;
            }
            if (res instanceof BotResponse r) {
                return r;
            }
            throw new BotApiException("Handler must return BotResponse");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BotApiException("Handler invocation error", e);
        }
    }

    @Override
    public @NonNull BotRequestType type() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull CommandMatch<BotApiObject> matcher() {
        return (CommandMatch<BotApiObject>) commandMatch;
    }

    @Override
    public @NonNull String botGroup() {
        return botGroup;
    }

    @Override
    public int order() {
        return order;
    }

    record ParamInfo(boolean request, Arg arg, BotArgumentConverter<?> converter) {
    }
}
