package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import io.lonmstalker.tgkit.core.BotRequest;

/** Редактирование сообщения. */
public final class EditBuilder extends BotResponse.CommonBuilder<EditBuilder> {
    private final long msgId;
    private Duration typing;
    private String newText;

    EditBuilder(BotRequest<?> req, long msgId) {
        super(req);
        this.msgId = msgId;
    }

    /** Показать набор текста перед редактированием. */
    public EditBuilder typing(Duration d) {
        this.typing = d;
        return this;
    }

    /** Текст после редактирования. */
    public EditBuilder thenEdit(String text) {
        this.newText = text;
        return this;
    }

    @Override
    protected BotApiMethod<?> build() {
        if (typing != null) {
            SendChatAction act = new SendChatAction();
            act.setChatId(String.valueOf(chatId));
            act.setAction("typing");
            CONFIG.transport.execute(act);
        }
        EditMessageText edit = new EditMessageText();
        edit.setChatId(String.valueOf(chatId));
        edit.setMessageId((int) msgId);
        edit.setText(newText);
        return edit;
    }
}
