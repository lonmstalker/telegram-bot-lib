package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.ttl.DeleteTask;
import io.lonmstalker.tgkit.core.ttl.TtlPolicy;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public final class WithTtl<T extends BotDSL.CommonBuilder<T, D>, D extends PartialBotApiMethod<?>> {
    private final @NonNull Duration d;
    private final @NonNull DSLContext ctx;
    private final BotDSL.@NonNull CommonBuilder<T, D> b;

    private TtlPolicy policy = TtlPolicy.defaults();
    private Consumer<Exception> onError = ex -> {
    };
    private Consumer<Long> onSuccess = id -> {
    };

    WithTtl(@NonNull Duration d,
            @NonNull DSLContext ctx,
            BotDSL.@NonNull CommonBuilder<T, D> b) {
        this.b = b;
        this.d = d;
        this.ctx = ctx;
    }

    public @NonNull WithTtl<T, D> onError(@NonNull Consumer<Exception> c) {
        onError = c;
        return this;
    }

    public @NonNull WithTtl<T, D> onSuccess(@NonNull Consumer<Long> c) {
        onSuccess = c;
        return this;
    }

    public @NonNull WithTtl<T, D> policy(@NonNull TtlPolicy p) {
        policy = p;
        return this;
    }

    public Common<T, D> done() {
        /*
         * Подписываемся на success-callback билдерa ОДИН раз.
         * После каждой фактической отправки сообщения планируем DeleteMessage.
         */
        b.hooks(
                msgId -> {
                    Long chat = ctx.userInfo().chatId();
                    Runnable action = () -> ctx.service().sender()
                            .execute(new DeleteMessage(Objects.requireNonNull(chat).toString(),
                                    msgId.intValue()));
                    TtlScheduler.instance()
                            .schedule(new DeleteTask(chat, msgId, action), d, policy);
                    onSuccess.accept(msgId);
                },
                ex -> onError.accept((Exception) ex)
        );

        return b;
    }
}