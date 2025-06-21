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
package io.github.tgkit.security.captcha;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Хранилище каптч в памяти, использующее кэш Caffeine.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * MathCaptchaProviderStore store = new InMemoryMathCaptchaProviderStore(Duration.ofMinutes(2), 1000);
 * }</pre>
 */
public class InMemoryMathCaptchaProviderStore implements MathCaptchaProviderStore {
  private final Cache<Long, Integer> cache;

  public InMemoryMathCaptchaProviderStore(Duration ttl, long maxSize) {
    this.cache = Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(maxSize).build();
  }

  public void put(long c, int a, @NonNull Duration __) {
    cache.put(c, a);
  }

  public Integer pop(long c) {
    return cache.asMap().remove(c);
  }
}
