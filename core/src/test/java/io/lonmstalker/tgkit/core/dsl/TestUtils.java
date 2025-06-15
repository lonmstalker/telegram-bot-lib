package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.Set;

/** Utility helpers for DSL tests. */
final class TestUtils {
    private TestUtils() {}

    static BotRequest<Message> request(long chatId) {
        BotInfo info = new BotInfo(1L, new InMemoryStateStore(),
                new TelegramSender(BotConfig.builder().build(), "T"),
                new MessageLocalizer(Locale.US));
        BotUserInfo user = new BotUserInfo() {
            @Override public @NonNull String chatId() { return String.valueOf(chatId); }
            @Override public @NonNull Set<String> roles() { return Set.of(); }
        };
        return new BotRequest<>(0, new Message(), info, user);
    }
}
