package io.lonmstalker.tgkit.core.ttl;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Планировщик задач TTL-delete.
 * 1️⃣  Свой кастомный пул (work-stealing, например)
 * ForkJoinPool fjp = new ForkJoinPool(4);
 * TtlScheduler.setFactory(() -> new Default(() -> fjp));
 * <p>
 * 2️⃣  Внутри DSL:
 * TtlScheduler.instance()
 *      .schedule(new DeleteTask(chatId,msgId, () -> sender.execute(del)),
 *          Duration.ofSeconds(30));
 */
public interface TtlScheduler extends AutoCloseable {

    @NonNull
    CompletableFuture<Void> schedule(
            @NonNull DeleteTask task,
            @NonNull Duration delay,
            @NonNull TtlPolicy policy);

    default @NonNull CompletableFuture<Void> schedule(
            @NonNull DeleteTask task,
            @NonNull Duration delay) {
        return schedule(task, delay, TtlPolicy.defaults());
    }

    /**
     * Фабрика назначается один раз при bootstrap-е приложения.
     */
    static void setFactory(@NonNull Supplier<? extends TtlScheduler> f) {
        TtlHolder.FACTORY = f;
    }

    /**
     * Безопасная точка входа, используемая DSL-ом.
     */
    static @NonNull TtlScheduler instance() {
        return TtlHolder.singleton();
    }
}