package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotInfo;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.interceptor.BotInterceptor;
import io.lonmstalker.core.storage.BotRequestHolder;
import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import io.lonmstalker.core.utils.UpdateUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

@Slf4j
public class BotAdapterImpl implements BotAdapter {

    private final Bot bot;
    private final BotRequestConverter<BotApiObject> converter;
    private final BotUserProvider userProvider;

    public BotAdapterImpl(@NonNull Bot bot,
                          @NonNull BotRequestConverter<BotApiObject> converter,
                          @NonNull BotUserProvider userProvider) {
        this.bot = bot;
        this.converter = converter;
        this.userProvider = userProvider;
    }

    @Override
    public @Nullable BotApiMethod<?> handle(@NonNull Update update) {
        List<BotInterceptor> interceptors = bot.config().getGlobalInterceptors();
        interceptors.forEach(i -> i.preHandle(update));
        BotResponse response = null;
        try {
            BotRequestType type = UpdateUtils.getType(update);
            BotApiObject data = converter.convert(update, type);
            BotCommand<BotApiObject> command = bot.registry().find(type, data);
            if (command == null) {
                return null;
            }
            var sender = new TelegramSender(bot.config(), bot.token());
            BotRequestHolder.setUpdate(update);
            BotRequestHolder.setSender(sender);
            BotUserInfo user = userProvider.resolve(update);
            BotInfo info = new BotInfo(bot.internalId(), sender, bot.config().getStore());
            response = command.handle(new BotRequest<>(update.getUpdateId(), data, info, user));
            interceptors.forEach(i -> i.postHandle(update));
            return response != null ? response.getMethod() : null;
        } finally {
            for (BotInterceptor i : interceptors) {
                try {
                    i.afterCompletion(update, response);
                } catch (Exception e) {
                    log.error("Interceptor afterCompletion error", e);
                }
            }
            BotRequestHolder.clear();
        }
    }
}
