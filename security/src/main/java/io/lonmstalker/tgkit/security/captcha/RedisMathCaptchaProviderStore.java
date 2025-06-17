package io.lonmstalker.tgkit.security.captcha;

import org.checkerframework.checker.nullness.qual.NonNull;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

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
            var res = j.getDel(k);                   // Redis >= 6.2
            return res != null ? Integer.valueOf(res) : null;
        }
    }
}
