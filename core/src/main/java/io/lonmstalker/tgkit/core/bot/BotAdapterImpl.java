package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestConverter;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import io.lonmstalker.tgkit.core.update.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Slf4j
public class BotAdapterImpl implements BotAdapter, AutoCloseable {
    private static final BotRequestConverter<BotApiObject> DEFAULT_CONVERTER = new BotRequestConverterImpl();

    private final @NonNull BotInfo botInfo;
    private final @NonNull BotConfig config;
    private final @NonNull TelegramSender sender;
    private final @NonNull BotCommandRegistry registry;
    private final @NonNull BotUserProvider userProvider;
    private final @NonNull List<BotInterceptor> interceptors;
    private final @NonNull BotRequestConverter<BotApiObject> converter;

    public BotAdapterImpl(long internalId,
                          @NonNull String token,
                          @NonNull BotConfig config,
                          @NonNull BotUserProvider userProvider,
                          @NonNull BotCommandRegistry registry,
                          @NonNull List<BotInterceptor> interceptors,
                          @NonNull MessageLocalizer messageLocalizer) {
        this.config = config;
        this.registry = registry;
        this.interceptors = interceptors;
        this.userProvider = userProvider;
        this.converter = DEFAULT_CONVERTER;
        this.sender = new TelegramSender(config, token);
        this.botInfo = new BotInfo(internalId, config.getStore(), sender, messageLocalizer);
    }

    @Override
    public @Nullable BotApiMethod<?> handle(@NonNull Update update) {
        Exception error = null;
        BotResponse response = null;
        try {
            BotRequestHolder.setUpdate(update);
            BotRequestHolder.setSender(sender);
            BotRequestHolder.setRequestId(String.valueOf(update.getUpdateId()));

            interceptors.forEach(i -> i.preHandle(update));
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

    private @Nullable BotResponse doHandle(@NonNull Update update) {
        BotRequestType type = UpdateUtils.getType(update);
        BotApiObject data = converter.convert(update, type);
        BotCommand<BotApiObject> command = registry.find(type, config.getBotGroup(), data);
        if (command == null) {
            return null;
        }
        try {
            BotUserInfo user = userProvider.resolve(update);
            Locale locale = resolveLocale(update, user);

            botInfo.localizer().setLocale(locale);
            return command.handle(new BotRequest<>(update.getUpdateId(), data, botInfo, user, locale));
        } finally {
            botInfo.localizer().resetLocale();
        }
    }

    private @NonNull Locale resolveLocale(@NonNull Update update,
                                          @NonNull BotUserInfo user) {
        Locale locale = user.locale();
        if (locale != null) {
            return locale;
        }
        User tgUser = null;
        try {
            tgUser = UpdateUtils.getUser(update);
        } catch (Exception ignored) {
            // no telegram user
        }
        if (tgUser != null && tgUser.getLanguageCode() != null && !tgUser.getLanguageCode().isBlank()) {
            return Locale.forLanguageTag(tgUser.getLanguageCode());
        }
        return config.getLocale();
    }

    private void afterCompletion(@NonNull Update update,
                                 @Nullable BotResponse response,
                                 @Nullable Exception error,
                                 @NonNull List<BotInterceptor> interceptors) {
        for (BotInterceptor i : interceptors) {
            try {
                i.afterCompletion(update, response, error);
            } catch (Exception e) {
                log.error("Interceptor afterCompletion error", e);
            }
        }
        RouteContextHolder.clear();
    }

    @Override
    public void close() throws IOException {
        try {
            sender.close();
        } catch (Exception e) {
            log.warn("Error closing sender", e);
        }
    }
}
