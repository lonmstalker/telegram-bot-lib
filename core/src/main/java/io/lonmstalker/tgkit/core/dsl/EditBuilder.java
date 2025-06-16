package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import io.lonmstalker.tgkit.core.BotRequest;

/** Редактирование сообщения. */
@SuppressWarnings("initialization.fields.uninitialized")
public final class EditBuilder extends BotDSL.CommonBuilder<EditBuilder> {
    private final long msgId;
    private Duration typing;
    private String newText;

    EditBuilder(@NonNull BotRequest<?> req, long msgId) {
        super(req);
        this.msgId = msgId;
    }

    /** Показать набор текста перед редактированием. */
    public @NonNull EditBuilder typing(@NonNull Duration d) {
        this.typing = d;
        return this;
    }

    /** Текст после редактирования. */
    public @NonNull EditBuilder thenEdit(@NonNull String text) {
        this.newText = text;
        return this;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        if (typing != null) {
            SendChatAction act = new SendChatAction();
            act.setChatId(String.valueOf(chatId));
            act.setAction(ActionType.TYPING);
            DslGlobalConfig.INSTANCE.getTransport().execute(act);
        }
        EditMessageText edit = new EditMessageText();
        edit.setChatId(String.valueOf(chatId));
        edit.setMessageId((int) msgId);
        edit.setText(newText);
        return edit;
    }
}
