package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.*;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.i18n.NoopMessageLocalizer;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import io.lonmstalker.tgkit.core.update.UpdateUtils;
import io.lonmstalker.tgkit.core.user.SimpleUserProvider;
import io.lonmstalker.tgkit.core.user.store.InMemoryUserKVStore;
import io.lonmstalker.tgkit.core.user.store.UserKVStore;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
public class BotAdapterImpl implements BotAdapter, AutoCloseable {
    private static final BotRequestConverter<BotApiObject> DEFAULT_CONVERTER = new BotRequestConverterImpl();

    private final @NonNull BotInfo botInfo;
    private final @NonNull BotConfig config;
    private final @NonNull BotService service;
    private final @NonNull BotCommandRegistry registry;
    private final @NonNull BotUserProvider userProvider;
    private final @NonNull List<BotInterceptor> interceptors;
    private final @NonNull BotRequestConverter<BotApiObject> converter;

    @Setter
    private @Nullable TelegramSender sender;

    @Builder
    public BotAdapterImpl(long internalId,
                          @Nullable BotConfig config,
                          @NonNull TelegramSender sender,
                          @Nullable UserKVStore userKVStore,
                          @Nullable BotUserProvider userProvider,
                          @Nullable BotCommandRegistry registry,
                          @Nullable List<BotInterceptor> interceptors,
                          @Nullable MessageLocalizer messageLocalizer) {
        this.sender = sender;
        this.config = config != null ? config : BotConfig.builder().build();
        this.registry = registry != null ? registry : new BotCommandRegistryImpl();
        this.interceptors = interceptors != null ? interceptors : Collections.emptyList();
        this.userProvider = userProvider != null ? userProvider : new SimpleUserProvider();
        this.converter = DEFAULT_CONVERTER;
        this.botInfo = new BotInfo(internalId);
        this.service = new BotService(
                this.config.getStore(),
                sender,
                userKVStore == null ? new InMemoryUserKVStore() : userKVStore,
                messageLocalizer != null ? messageLocalizer : new NoopMessageLocalizer()
        );
    }

    @Override
    public @Nullable BotApiMethod<?> handle(@NonNull Update update) {
        checkStarted();
        Exception error = null;
        Pair<BotResponse, BotRequest<?>> response = null;
        try {
            BotRequestHolder.setUpdate(update);
            BotRequestHolder.setSender(Objects.requireNonNull(sender));
            BotRequestHolder.setRequestId(String.valueOf(update.getUpdateId()));

            response = doHandle(update);
            return response != null ? response.getKey().getMethod() : null;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            afterCompletion(update, response, error, interceptors);
            BotRequestHolder.clear();
            service.localizer().resetLocale();
        }
    }

    private @Nullable Pair<BotResponse, BotRequest<?>> doHandle(@NonNull Update update) {
        try {
            BotRequestType type = UpdateUtils.getType(update);
            BotApiObject data = converter.convert(update, type);

            BotUserInfo user = userProvider.resolve(update);
            Locale locale = resolveLocale(update, user);

            service.localizer().setLocale(locale);
            BotRequest<BotApiObject> request = new BotRequest<>(
                    update.getUpdateId(),
                    data,
                    locale,
                    UpdateUtils.resolveMessageId(update),
                    botInfo,
                    user,
                    service,
                    type
            );

            interceptors.forEach(i -> i.preHandle(update, request));
            BotCommand<BotApiObject> command = registry.find(type, config.getBotGroup(), data);
            if (command == null) {
                return null;
            }

            var result = command.handle(request);
            interceptors.forEach(i -> i.postHandle(update, request));

            return Pair.of(result, request);
        } finally {
            service.localizer().resetLocale();
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
                                 @Nullable Pair<BotResponse, BotRequest<?>> response,
                                 @Nullable Exception error,
                                 @NonNull List<BotInterceptor> interceptors) {
        for (BotInterceptor i : interceptors) {
            try {
                if (response != null) {
                    i.afterCompletion(update, response.getRight(), response.getLeft(), error);
                } else {
                    i.afterCompletion(update, null, null, error);
                }
            } catch (Exception e) {
                log.error("Interceptor afterCompletion error", e);
            }
        }
        RouteContextHolder.clear();
    }

    @Override
    public void close() throws IOException {
        try {
            Objects.requireNonNull(sender).close();
        } catch (Exception e) {
            log.warn("Error closing sender", e);
        }
    }

    private void checkStarted() {
        if (sender == null) {
            throw new IllegalStateException("Bot adapter not started");
        }
    }
}
