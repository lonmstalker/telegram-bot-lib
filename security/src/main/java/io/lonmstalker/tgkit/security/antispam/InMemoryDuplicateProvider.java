package io.lonmstalker.tgkit.security.antispam;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InMemoryDuplicateProvider implements DuplicateProvider {
  private final Cache<Long, Set<Integer>> cache;

  @Builder
  public InMemoryDuplicateProvider(@NonNull Duration ttl, long maxSize) {
    this.cache = Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(maxSize).build();
  }

  @Override
  public boolean isDuplicate(long chat, @NonNull String text) {
    int h = text.hashCode();
    return !Objects.requireNonNull(cache.get(chat, __ -> ConcurrentHashMap.newKeySet())).add(h);
  }
}
