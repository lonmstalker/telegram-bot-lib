package io.lonmstalker.tgkit.security.antispam;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InMemoryDuplicateProvider implements DuplicateProvider {
  private final Cache<Long, Set<Integer>> cache;

  private InMemoryDuplicateProvider(@NonNull Duration ttl, long maxSize) {
    this.cache = Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(maxSize).build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Duration ttl;
    private long maxSize;

    public Builder ttl(@NonNull Duration ttl) {
      this.ttl = ttl;
      return this;
    }

    public Builder maxSize(long maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    public InMemoryDuplicateProvider build() {
      return new InMemoryDuplicateProvider(ttl, maxSize);
    }
  }

  @Override
  /**
   * Проверяет текст на повтор в рамках чата.
   */
  public boolean isDuplicate(long chat, @NonNull String text) {
    int h = text.hashCode();
    return !Objects.requireNonNull(cache.get(chat, __ -> ConcurrentHashMap.newKeySet())).add(h);
  }
}
