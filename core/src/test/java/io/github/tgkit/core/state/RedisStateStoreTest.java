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
package io.github.tgkit.core.state;

import static org.mockito.Mockito.*;

import io.github.tgkit.testkit.TestBotBootstrap;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

class RedisStateStoreTest implements WithAssertions {
  static {
    TestBotBootstrap.initOnce();
  }

  private JedisPool pool;
  private Jedis jedis;
  private RedisStateStore store;

  @BeforeEach
  void init() {
    pool = mock(JedisPool.class);
    jedis = mock(Jedis.class);
    when(pool.getResource()).thenReturn(jedis);
    store = new RedisStateStore(pool);
  }

  @Test
  void get_reads_value_from_redis() {
    when(jedis.get("42")).thenReturn("STEP");

    assertThat(store.get("42")).isEqualTo("STEP");
    verify(jedis).close();
  }

  @Test
  void set_writes_value_to_redis() {
    store.set("77", "NEXT");

    verify(jedis).set("77", "NEXT");
    verify(jedis).close();
  }
}
