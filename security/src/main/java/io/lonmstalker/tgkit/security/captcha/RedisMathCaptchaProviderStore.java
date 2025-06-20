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
package io.lonmstalker.tgkit.security.captcha;

import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;
import redis.clients.jedis.JedisPool;

/**
 * Хранит ответы каптч в Redis с ограничением времени жизни.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * MathCaptchaProviderStore store = new RedisMathCaptchaProviderStore(pool);
 * }</pre>
 */
public class RedisMathCaptchaProviderStore implements MathCaptchaProviderStore {
  private final JedisPool pool;

  public RedisMathCaptchaProviderStore(@NonNull JedisPool pool) {
    this.pool = pool;
  }

  public void put(long c, int a, @NonNull Duration ttl) {
    try (var j = pool.getResource()) {
      j.setex(("captcha:" + c), (int) ttl.getSeconds(), String.valueOf(a));
    }
  }

  public Integer pop(long c) {
    try (var j = pool.getResource()) {
      String k = "captcha:" + c;
      var res = j.getDel(k); // Redis >= 6.2
      return res != null ? Integer.valueOf(res) : null;
    }
  }
}
