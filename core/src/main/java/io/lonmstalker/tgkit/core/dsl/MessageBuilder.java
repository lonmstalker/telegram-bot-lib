package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.parse_mode.Sanitizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import io.lonmstalker.tgkit.core.BotRequest;

/**
 * Создание текстового сообщения.
 */
public final class MessageBuilder extends BotResponse.CommonBuilder<MessageBuilder> {
    private final String text;
    private ParseMode parseMode;

    MessageBuilder(BotRequest<?> req, String text) {
        super(req);
        this.text = text;
    }

    public @NonNull MessageBuilder parseMode(@NonNull ParseMode mode) {
        this.parseMode = mode;
        return this;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        ParseMode p = parseMode != null ? parseMode : DslGlobalConfig.INSTANCE.getParseMode();
        String t = Sanitizer.sanitize(text, p);

        SendMessage msg = new SendMessage(String.valueOf(chatId), t);
        msg.setParseMode(p.getMode());
        msg.setDisableNotification(disableNotif);

        if (replyTo != null) {
            msg.setReplyToMessageId(replyTo.intValue());
        }
        if (keyboard != null) {
            msg.setReplyMarkup(keyboard.build());
        }

        return msg;
    }
}
