package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Общие операции билдера.
 */
public interface Common<T extends Common<T>> {

    @NonNull
    T requireChatId();

    @NonNull
    T missingIdStrategy(@NonNull MissingIdStrategy strategy);

    @NonNull
    T replyTo(long msgId);

    @NonNull
    T disableNotif();

    @NonNull
    T keyboard(@NonNull Consumer<KbBuilder> cfg);

    @NonNull
    T when(@NonNull Predicate<DSLContext> cond,
           @NonNull Consumer<Common<T>> branch);

    @NonNull
    T onlyAdmin(@NonNull Consumer<Common<T>> branch);

    @NonNull
    T ifFlag(@NonNull String flag,
             @NonNull Consumer<Common<T>> branch);

    @NonNull
    T flag(@NonNull String flag,
           @NonNull Consumer<Common<T>> branch);

    // проверка для userId
    @NonNull
    T flagUser(@NonNull String flag,
               @NonNull Consumer<Common<T>> branch);

    // A/B-сплит: control / variant
    @NonNull
    T abTest(@NonNull String key,
             @NonNull Consumer<Common<T>> control,
             @NonNull Consumer<Common<T>> variant);

    @NonNull
    T ttl(@NonNull Duration duration);

    @NonNull
    T hooks(@NonNull Consumer<Long> ok, @NonNull Consumer<Throwable> fail);

    @NonNull
    BotResponse send();

    @NonNull
    CompletableFuture<BotResponse> sendAsync(@NonNull Executor executor);

    @NonNull
    PartialBotApiMethod<?> build();
}
