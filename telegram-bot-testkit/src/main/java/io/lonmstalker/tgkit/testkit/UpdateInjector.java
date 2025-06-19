package io.lonmstalker.tgkit.testkit;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Утилита для инъекции тестовых {@link Update} в {@link BotAdapter}.
 */
public final class UpdateInjector {

    private final BotAdapter adapter;
    private final TelegramSender sender;
    private final AtomicInteger nextId = new AtomicInteger();

    public UpdateInjector(@NonNull BotAdapter adapter, @NonNull TelegramSender sender) {
        this.adapter = adapter;
        this.sender = sender;
    }

    /** Создаёт Update с текстовым сообщением. */
    public Builder text(String text) {
        Message msg = new Message();
        msg.setText(text);
        Update update = new Update();
        update.setMessage(msg);
        return new Builder(update);
    }

    /** Билдер для указания параметров Update. */
    public final class Builder {
        private final Update update;

        private Builder(Update update) {
            this.update = update;
        }

        /** Устанавливает отправителя и чат. */
        public Builder from(long id) {
            Chat chat = new Chat();
            chat.setId(id);
            User user = new User();
            user.setId(id);
            Objects.requireNonNull(update.getMessage()).setChat(chat);
            update.getMessage().setFrom(user);
            return this;
        }

        /** Отправляет Update в бота. */
        public void dispatch() {
            update.setUpdateId(nextId.incrementAndGet());
            BotApiMethod<?> method = adapter.handle(update);
            if (method != null) {
                sender.execute(method);
            }
        }
    }
}
