package io.lonmstalker.core.loader;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotHandlerConverter;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.matching.CommandMatch;
import lombok.AccessLevel;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Builder(access = AccessLevel.PACKAGE)
class InternalCommandAdapter implements BotCommand<BotApiObject> {
    private final int order;
    private final @NonNull Method method;
    private final @NonNull Object instance;
    private final @NonNull String botGroup;
    private final @NonNull BotRequestType type;
    private final @NonNull BotHandlerConverter<?> converter;
    private final @NonNull CommandMatch<? extends BotApiObject> commandMatch;

    @Override
    public @Nullable BotResponse handle(@NonNull BotRequest<BotApiObject> request) {
        try {
            Object arg = converter.convert(request);
            Object res = method.invoke(instance, arg);
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
}
