package io.lonmstalker.core.storage;

import io.lonmstalker.core.bot.TelegramSender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotRequestHolder {
    private static final ThreadLocal<Update> UPDATE = new ThreadLocal<>();
    private static final ThreadLocal<TelegramSender> SENDER = new ThreadLocal<>();

    public static void setUpdate(Update update) {
        UPDATE.set(update);
    }

    public static void setSender(TelegramSender sender) {
        SENDER.set(sender);
    }

    public static void clear() {
        UPDATE.remove();
        SENDER.remove();
    }

    public static Update getUpdate() {
        return UPDATE.get();
    }

    public static TelegramSender getSender() {
        return SENDER.get();
    }
}
