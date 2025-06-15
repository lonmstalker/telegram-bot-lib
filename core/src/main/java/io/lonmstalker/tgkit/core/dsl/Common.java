package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/** Общие операции билдера. */
public interface Common<T extends Common<T>> {
    T chat(long id);

    T autoChat(BotRequest<?> req);

    T replyTo(long msgId);

    T disableNotif();

    T keyboard(java.util.function.Consumer<KbBuilder> cfg);

    T when(Predicate<Context> cond, Consumer<? extends Common<?>> branch);

    T onlyAdmin(Consumer<? extends Common<?>> branch);

    T ifFlag(String flag, Context ctx, Consumer<? extends Common<?>> branch);

    T flag(String flag, Context ctx, Consumer<? extends Common<?>> branch);

    T ttl(Duration duration);

    T hooks(Consumer<Long> ok, Consumer<Throwable> fail);

    BotResponse send(TelegramTransport tg);

    CompletableFuture<BotResponse> sendAsync(Executor executor);

    BotApiMethod<?> build();
}
