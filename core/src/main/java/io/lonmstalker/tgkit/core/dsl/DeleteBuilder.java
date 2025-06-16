package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import io.lonmstalker.tgkit.core.BotRequest;

/** Удаление сообщения. */
public final class DeleteBuilder extends BotDSL.CommonBuilder<DeleteBuilder> {
    private final long msgId;

    DeleteBuilder(@NonNull BotRequest<?> req, long msgId) {
        super(req);
        this.msgId = msgId;
    }

    @Override
    public @NonNull BotApiMethod<?> build() {
        return new DeleteMessage(String.valueOf(chatId), (int) msgId);
    }
}
