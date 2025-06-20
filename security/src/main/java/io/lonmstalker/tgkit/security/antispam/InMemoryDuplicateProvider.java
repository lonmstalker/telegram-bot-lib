/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lonmstalker.tgkit.security.antispam;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Проверяет дубликаты сообщений, используя кэш в памяти.
 *
 * <p>Пример создания через builder:
 * <pre>{@code
 * DuplicateProvider provider = InMemoryDuplicateProvider.builder()
 *     .ttl(Duration.ofMinutes(1))
 *     .maxSize(1000)
 *     .build();
 * }
 * </pre>
 */
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
  /** Проверяет текст на повтор в рамках чата. */
  public boolean isDuplicate(long chat, @NonNull String text) {
    int h = text.hashCode();
    return !Objects.requireNonNull(cache.get(chat, __ -> ConcurrentHashMap.newKeySet())).add(h);
  }
}
