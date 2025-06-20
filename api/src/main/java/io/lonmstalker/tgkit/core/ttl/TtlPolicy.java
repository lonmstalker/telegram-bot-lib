package io.lonmstalker.tgkit.core.ttl;

import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface TtlPolicy {

  int maxRetries();

  @NonNull Duration initialBackOff();

  @NonNull Duration maxBackOff();

  static TtlPolicy defaults() {
    return new TtlPolicy() {

      @Override
      public int maxRetries() {
        return 7;
      }

      @Override
      public @NonNull Duration initialBackOff() {
        return Duration.ofSeconds(2);
      }

      @Override
      public @NonNull Duration maxBackOff() {
        return Duration.ofSeconds(5);
      }
    };
  }
}
