package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestConverter;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.craftbot.render.spi.ResponseDispatcher;
import io.lonmstalker.tgkit.core.args.Context;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import io.lonmstalker.tgkit.core.utils.UpdateUtils;
import io.craftbot.security.CaptchaRequired;
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
    private final ResponseDispatcher dispatcher;

    public BotAdapterImpl(@NonNull Bot bot,
                          @NonNull BotUserProvider userProvider) {
        this(bot, new BotRequestConverterImpl(), userProvider);
    }

    public BotAdapterImpl(@NonNull Bot bot,
                          @NonNull BotRequestConverter<BotApiObject> converter,
                          @NonNull BotUserProvider userProvider) {
        this.bot = bot;
        this.converter = converter;
        this.userProvider = userProvider;
        this.dispatcher = new ResponseDispatcher(bot.getClass().getClassLoader());
    }

    @Override
    public @Nullable BotApiMethod<?> handle(@NonNull Update update) {
        List<BotInterceptor> interceptors = bot.config().getGlobalInterceptors();
        interceptors.forEach(i -> i.preHandle(update));
        BotResponse response = null;
        Exception error = null;
        try {
            response = doHandle(update);
            interceptors.forEach(i -> i.postHandle(update));
            return response != null ? response.getMethod() : null;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            afterCompletion(update, response, error, interceptors);
            BotRequestHolder.clear();
        }
    }

    private @Nullable BotResponse doHandle(Update update) throws Exception {
        BotRequestType type = UpdateUtils.getType(update);
        BotApiObject data = converter.convert(update, type);
        BotCommand<BotApiObject> command = bot.registry().find(type, bot.config().getBotPattern(), data);
        if (command == null) {
            return null;
        }
        var sender = createSender();
        try {
            BotRequestHolder.setUpdate(update);
            BotRequestHolder.setSender(sender);
            BotUserInfo user = userProvider.resolve(update);

            java.util.Locale locale = user.locale();
            if (locale == null) {
                org.telegram.telegrambots.meta.api.objects.User tgUser = null;
                try {
                    tgUser = UpdateUtils.getUser(update);
                } catch (Exception ignored) {
                    // no telegram user
                }
                if (tgUser != null && tgUser.getLanguageCode() != null && !tgUser.getLanguageCode().isBlank()) {
                    locale = java.util.Locale.forLanguageTag(tgUser.getLanguageCode());
                } else {
                    locale = bot.config().getLocale();
                }
            }

            var localizer = new MessageLocalizer(locale);
            BotInfo info = new BotInfo(bot.internalId(), bot.config().getStore(), sender, localizer);
            var request = new BotRequest<>(update.getUpdateId(), data, info, user);
            Context ctx = new Context(request, null);
            Object res;
            try {
                res = command.handle(request);
            } catch (io.craftbot.security.CaptchaRequired cr) {
                return dispatcher.toResponse(cr.challenge(), ctx);
            }
            if (res == null) return null;
            return dispatcher.toResponse(res, ctx);
        } finally {
            try {
                sender.close();
            } catch (Exception e) {
                log.warn("Error closing sender", e);
            }
        }
    }

    private TelegramSender createSender() {
        return new TelegramSender(bot.config(), bot.token());
    }

    private void afterCompletion(Update update, @Nullable BotResponse response, @Nullable Exception error, List<BotInterceptor> interceptors) {
        for (BotInterceptor i : interceptors) {
            try {
                i.afterCompletion(update, response, error);
            } catch (Exception e) {
                log.error("Interceptor afterCompletion error", e);
            }
        }
        RouteContextHolder.clear();
    }
}
