package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

/** Удаление сообщения. */
public final class DeleteBuilder extends BotResponse.CommonBuilder<DeleteBuilder> {
    private final long msgId;

    DeleteBuilder(long msgId) {
        this.msgId = msgId;
    }

    @Override
    protected BotApiMethod<?> build() {
        DeleteMessage dm = new DeleteMessage(String.valueOf(chatId), (int) msgId);
        return dm;
    }
}
