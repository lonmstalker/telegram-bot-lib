package io.lonmstalker.tgkit.core.state;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;
import redis.embedded.RedisServer;

public class RedisStateStoreTest {
  private static RedisServer redis;
  private static JedisPool pool;

  @BeforeAll
  static void startRedis() throws Exception {
    redis = new RedisServer();
    redis.start();
    pool = new JedisPool("localhost", redis.ports().get(0));
  }

  @AfterAll
  static void stopRedis() {
    pool.close();
    redis.stop();
  }

  @Test
  void returnsNullWhenAbsent() {
    RedisStateStore store = new RedisStateStore(pool);
    assertNull(store.get("1"));
  }

  @Test
  void setAndGetValue() {
    RedisStateStore store = new RedisStateStore(pool);
    store.set("1", "A");
    assertEquals("A", store.get("1"));
  }
}
