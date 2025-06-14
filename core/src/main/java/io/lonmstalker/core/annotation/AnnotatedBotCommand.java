package io.lonmstalker.core.annotation;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotInfo;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.storage.BotRequestHolder;
import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import io.lonmstalker.core.matching.CommandMatch;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import io.lonmstalker.core.exception.BotApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Command created from annotated method.
 */
@Slf4j
@Builder
public class AnnotatedBotCommand implements BotCommand<BotApiObject> {

    private final Object handler;
    private final Method method;
    private final BotRequestConverter<?> converter;
    private final CommandMatch<BotApiObject> matcher;
    private final BotRequestType type;
    private final int order;
    private final BotUserProvider userProvider;


    @Override
    public @NonNull BotResponse handle(@NonNull BotRequest<BotApiObject> request) {
        try {
            Update update = BotRequestHolder.getUpdate();
            Object data = converter.convert(update, type);
            BotUserInfo user = userProvider.resolve(update);
            BotInfo info = new BotInfo(request.botInfo().botId(), BotRequestHolder.getSender());
            BotRequest<Object> newReq = new BotRequest<>(update.getUpdateId(), data, info, user);
            BotResponse resp = (BotResponse) method.invoke(handler, newReq);
            if (resp == null) {
                log.debug("Handler {} returned null", method.getName());
                return new BotResponse();
            }
            return resp;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to invoke handler method", e);
            throw new BotApiException(e);
        }
    }

    @Override
    public @NonNull BotRequestType type() {
        return type;
    }

    @Override
    public @NonNull CommandMatch<BotApiObject> matcher() {
        return matcher;
    }

    @Override
    public int order() {
        return order;
    }
}
