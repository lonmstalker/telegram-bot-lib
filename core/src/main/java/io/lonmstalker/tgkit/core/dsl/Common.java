package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.lonmstalker.tgkit.core.BotResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Общие операции билдера.
 */
public interface Common<T extends Common<T>> {

    @NonNull
    T chat(long id);

    @NonNull
    T replyTo(long msgId);

    @NonNull
    T disableNotif();

    @NonNull
    T keyboard(@NonNull Consumer<KbBuilder> cfg);

    @NonNull
    T when(@NonNull Predicate<Context> cond,
           @NonNull Consumer<Common<T>> branch);

    @NonNull
    T onlyAdmin(@NonNull Consumer<Common<T>> branch);

    @NonNull
    T ifFlag(@NonNull String flag,
             @NonNull Context ctx,
             @NonNull Consumer<Common<T>> branch);

    @NonNull
    T flag(@NonNull String flag,
           @NonNull Context ctx,
           @NonNull Consumer<Common<T>> branch);

    @NonNull
    T ttl(@NonNull Duration duration);

    @NonNull
    T hooks(@NonNull Consumer<Long> ok, @NonNull Consumer<Throwable> fail);

    @NonNull
    BotResponse send(@NonNull TelegramTransport tg);

    @NonNull
    CompletableFuture<BotResponse> sendAsync(@NonNull Executor executor);

    @NonNull
    PartialBotApiMethod<?> build();
}
