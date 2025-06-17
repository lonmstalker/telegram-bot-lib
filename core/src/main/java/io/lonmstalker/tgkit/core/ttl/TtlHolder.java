package io.lonmstalker.tgkit.core.ttl;

import io.netty.channel.DefaultEventLoop;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TtlHolder {
    static Supplier<? extends TtlScheduler> FACTORY = Default::new;
    private static volatile TtlScheduler INSTANCE;

    static synchronized TtlScheduler singleton() {
        if (INSTANCE == null) INSTANCE = FACTORY.get();
        return INSTANCE;
    }

    static final class Default implements TtlScheduler {

        private final ScheduledExecutorService exec;
        private final Map<DeleteTask, RetryRunner> live = new ConcurrentHashMap<>();

        Default() {
            this(DefaultEventLoop::new);
            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        }

        Default(@NonNull Supplier<ScheduledExecutorService> execSup) {
            this.exec = execSup.get();
        }

        @Override
        public @NonNull CompletableFuture<Void> schedule(@NonNull DeleteTask task,
                                                         @NonNull Duration delay,
                                                         @NonNull TtlPolicy pol) {
            RetryRunner rr = new RetryRunner(task, pol);
            live.put(task, rr);
            rr.future = exec.schedule(rr, delay.toMillis(), TimeUnit.MILLISECONDS);
            return rr.promise;
        }

        @Override
        public void close() {
            exec.shutdown();
            try {
                exec.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        private final class RetryRunner implements Runnable {
            final DeleteTask task;
            final TtlPolicy pol;
            final AtomicInteger attempts = new AtomicInteger();
            final CompletableFuture<Void> promise = new CompletableFuture<>();
            volatile ScheduledFuture<?> future;

            RetryRunner(@NonNull DeleteTask t, @NonNull TtlPolicy p) {
                task = t;
                pol = p;
            }

            @Override
            public void run() {
                try {
                    task.action().run();
                    promise.complete(null);
                    live.remove(task);
                } catch (Exception ex) {
                    int n = attempts.incrementAndGet();
                    if (n > pol.maxRetries()) {
                        promise.completeExceptionally(ex);
                        live.remove(task);
                        return;
                    }
                    long next = Math.min(pol.initialBackOff().toMillis() * (1L << (n - 1)),
                            pol.maxBackOff().toMillis());
                    future = exec.schedule(this, next, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
