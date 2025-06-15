package io.lonmstalker.tgkit.core.dsl;

import org.apache.commons.lang3.StringEscapeUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Создание текстового сообщения. */
public final class MessageBuilder extends BotResponse.CommonBuilder<MessageBuilder> {
    private final String text;

    MessageBuilder(String text) {
        this.text = text;
    }

    @Override
    protected BotApiMethod<?> build() {
        String t = CONFIG.sanitize ? StringEscapeUtils.escapeHtml4(text) : text;
        SendMessage msg = new SendMessage(String.valueOf(chatId), t);
        msg.setParseMode(CONFIG.parseMode);
        if (replyTo != null) msg.setReplyToMessageId(replyTo.intValue());
        msg.setDisableNotification(disableNotif);
        if (keyboard != null) msg.setReplyMarkup(keyboard.build());
        return msg;
    }
}
