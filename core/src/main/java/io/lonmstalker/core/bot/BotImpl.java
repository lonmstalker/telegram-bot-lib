package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.bot.BotSessionImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BotImpl implements Bot {

    private long id;
    private volatile @Nullable User user;
    private @Nullable SetWebhook setWebhook;
    private @NonNull String token;
    private @NonNull BotConfig config;
    private @NonNull AbsSender absSender;
    private @NonNull BotCommandRegistry commandRegistry;
    private @Nullable BotSessionImpl session;

    @Builder.Default
    private @NonNull List<BotCompleteAction> completeActions = new ArrayList<>();

    @Override
    public long internalId() {
        checkStarted();
        return id;
    }

    @Override
    public long externalId() {
        checkStarted();
        return Objects.requireNonNull(user).getId();
    }

    @Override
    public void start() {
        checkNotStarted();
        try {
            this.user = absSender.execute(new GetMe());
            if (absSender instanceof LongPollingReceiver receiver) {
                receiver.setUsername(this.user.getUserName());
                if (this.session == null) {
                    this.session = new BotSessionImpl();
                }
                this.session.setOptions(config);
                this.session.setToken(token);
                this.session.setCallback(receiver);
                this.session.start();
            } else if (absSender instanceof WebHookReceiver receiver) {
                receiver.setUsername(this.user.getUserName());
                if (this.setWebhook != null) {
                    receiver.execute(this.setWebhook);
                }
            }
        } catch (Exception ex) {
            throw new BotApiException("Error starting bot", ex);
        }
    }

    @Override
    public void stop() {
        checkStarted();
        for (BotCompleteAction action : completeActions) {
            try {
                action.complete();
            } catch (Exception e) {
                log.error("Complete action error", e);
            }
        }
        if (session != null && session.isRunning()) {
            try {
                session.stop();
            } catch (Exception e) {
                log.warn("Error stopping session", e);
            }
        }
        if (absSender instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.warn("Error closing sender", e);
            }
        }
        this.user = null;
        this.setWebhook = null;
        this.session = null;
    }

    @Override
    public @NonNull String username() {
        checkStarted();
        return Objects.requireNonNull(user).getUserName();
    }

    @Override
    public @NonNull BotCommandRegistry registry() {
        return commandRegistry;
    }

    @Override
    public boolean isStarted() {
        return user != null;
    }

    @Override
    public void onComplete(@NonNull BotCompleteAction action) {
        completeActions.add(action);
    }

    @Override
    public @NonNull BotConfig config() {
        return this.config;
    }

    @Override
    public @NonNull String token() {
        return this.token;
    }

    private void checkStarted() {
        if (user == null) {
            throw new BotApiException("Bot not started");
        }
    }

    private void checkNotStarted() {
        if (user != null) {
            throw new BotApiException("Bot started");
        }
    }
}
