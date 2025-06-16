package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.validator.TextLengthValidator;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.parse_mode.Sanitizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Создание текстового сообщения.
 */
@SuppressWarnings("initialization.fields.uninitialized")
public final class MessageBuilder extends BotDSL.CommonBuilder<MessageBuilder> {
    private static final TextLengthValidator VALIDATOR = new TextLengthValidator();
    private final String text;
    private ParseMode parseMode;

    MessageBuilder(DSLContext ctx, String text) {
        super(ctx);
        this.text = text;
    }

    public @NonNull MessageBuilder parseMode(@NonNull ParseMode mode) {
        this.parseMode = mode;
        return this;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        requireChatId();

        ParseMode p = parseMode != null ? parseMode : DslGlobalConfig.INSTANCE.getParseMode();
        String t = Sanitizer.sanitize(text, p);

        VALIDATOR.validate(text);

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
