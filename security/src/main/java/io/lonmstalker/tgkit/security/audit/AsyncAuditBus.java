package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Высокопроизводительная MPSC-шина:
 * • публикация кладёт событие в {@link LinkedBlockingQueue} (O(1));
 * • один consumer читает очередь и развозит события подписчикам.
 * Executor можно переопределить через
 * SecurityGlobalConfig.audit().executor(custom).
 */
public class AsyncAuditBus implements AuditBus {

    private final BlockingQueue<AuditEvent> queue;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final List<Consumer<AuditEvent>> subscribers = new CopyOnWriteArrayList<>();

    /**
     * Создаёт bus на указанном executore.
     */
    @SuppressWarnings("methodref.receiver.bound")
    public AsyncAuditBus(@NonNull ExecutorService executor, int queueSize) {
        executor.submit(this::loop);
        this.queue = new LinkedBlockingQueue<>(queueSize);
    }

    /**
     * Используется конфигом по умолчанию (виртуальные треды).
     */
    public AsyncAuditBus() {
        this(BotGlobalConfig.INSTANCE.executors().getIoExecutorService(), 100);
    }

    @Override
    public void publish(@NonNull AuditEvent event) {
        queue.offer(event);
    }

    @Override
    public void subscribe(@NonNull Consumer<AuditEvent> handler) {
        subscribers.add(handler);
    }

    @Override
    public void unsubscribe(@NonNull Consumer<AuditEvent> handler) {
        subscribers.remove(handler);
    }

    @Override
    public @NonNull List<Consumer<AuditEvent>> getSubscribers() {
        return subscribers;
    }

    /* ────────────────── internal ────────────────── */

    private void loop() {
        while (!Thread.currentThread().isInterrupted() && !isClosed.get()) {
            AuditEvent ev;
            try {
                ev = queue.poll(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new BotApiException(e);
            }
            if (ev != null) {
                for (Consumer<AuditEvent> s : subscribers) {
                    try {
                        s.accept(ev);
                    } catch (Throwable t) {
                        // не ломаем цикл, логировать обязан сам подписчик
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        isClosed.set(true);
    }
}
