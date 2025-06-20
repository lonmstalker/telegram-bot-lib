package io.lonmstalker.tgkit.core.ttl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Планировщик задач TTL-delete. */
public interface TtlScheduler extends AutoCloseable {

  @NonNull CompletableFuture<Void> schedule(
      @NonNull DeleteTask task, @NonNull Duration delay, @NonNull TtlPolicy policy);

  default @NonNull CompletableFuture<Void> schedule(
      @NonNull DeleteTask task, @NonNull Duration delay) {
    return schedule(task, delay, TtlPolicy.defaults());
  }
}
