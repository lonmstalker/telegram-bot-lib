package io.lonmstalker.tgkit.core.storage;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotRequestHolder {
    private static final ThreadLocal<@Nullable Update> UPDATE = new ThreadLocal<>();
    private static final ThreadLocal<@Nullable TelegramSender> SENDER = new ThreadLocal<>();

    public static void setUpdate(@NonNull Update update) {
        UPDATE.set(update);
    }

    public static void setSender(@NonNull TelegramSender sender) {
        SENDER.set(sender);
    }

    public static void clear() {
        UPDATE.remove();
        SENDER.remove();
    }

    public static @Nullable Update getUpdate() {
        return UPDATE.get();
    }

    public static @Nullable TelegramSender getSender() {
        return SENDER.get();
    }

    public static @NonNull Update getUpdateNotNull() {
        return Optional.ofNullable(UPDATE.get())
                .orElseThrow(() -> new BotApiException("Update not set"));
    }

    public static @NonNull TelegramSender getSenderNotNull() {
        return Optional.ofNullable(SENDER.get())
                .orElseThrow(() -> new BotApiException("Sender not set"));
    }
}
