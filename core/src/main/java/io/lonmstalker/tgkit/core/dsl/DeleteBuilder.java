package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import io.lonmstalker.tgkit.core.BotRequest;

/** Удаление сообщения. */
public final class DeleteBuilder extends BotResponse.CommonBuilder<DeleteBuilder> {
    private final long msgId;

    DeleteBuilder(BotRequest<?> req, long msgId) {
        super(req);
        this.msgId = msgId;
    }

    @Override
    protected BotApiMethod<?> build() {
        DeleteMessage dm = new DeleteMessage(String.valueOf(chatId), (int) msgId);
        return dm;
    }
}
