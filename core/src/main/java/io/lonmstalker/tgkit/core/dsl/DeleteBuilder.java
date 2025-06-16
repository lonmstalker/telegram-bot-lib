package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

/** Удаление сообщения. */
public final class DeleteBuilder extends BotDSL.CommonBuilder<DeleteBuilder> {
    private final long msgId;

    DeleteBuilder(@NonNull DSLContext ctx, long msgId) {
        super(ctx);
        this.msgId = msgId;
    }

    @Override
    public @NonNull BotApiMethod<?> build() {
        return new DeleteMessage(String.valueOf(chatId), (int) msgId);
    }
}
