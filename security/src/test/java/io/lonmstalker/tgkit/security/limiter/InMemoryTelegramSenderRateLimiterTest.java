package io.lonmstalker.tgkit.security.limiter;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import io.lonmstalker.tgkit.security.ratelimit.impl.InMemoryRateLimiter;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class InMemoryTelegramSenderRateLimiterTest implements WithAssertions {
    static RateLimiter newLimiter() {
        return new InMemoryRateLimiter(10_000);
    }

    static RateLimiter newLimiter(Clock clock) {
        return new InMemoryRateLimiter(10_000, clock);
    }

    private static final class FakeClock extends Clock {
        private Instant now = Instant.EPOCH;

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(@SuppressWarnings("unused") ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }
    }

    static {
        BotCoreInitializer.init();
        BotSecurityInitializer.init();
    }

    /* ================================================================ *
     * 1)  basic window behaviour                                        *
     * ================================================================ */
    @Test void allowsUpToPermitsAndRejectsAfter() {
        var rl = newLimiter();

        String key = "cmd:foo:global";
        assertThat(rl.tryAcquire(key, 3, 60)).isTrue();
        assertThat(rl.tryAcquire(key, 3, 60)).isTrue();
        assertThat(rl.tryAcquire(key, 3, 60)).isTrue();

        // 4-й запрос в том же окне
        assertThat(rl.tryAcquire(key, 3, 60)).isFalse();
    }

    /* ================================================================ *
     * 2)  counter resets on next window                                 *
     * ================================================================ */
    @Test void counterResetsAfterWindow() {
        FakeClock clock = new FakeClock();
        var rl = newLimiter(clock);
        String k = "cmd:bar:global";

        // permits =1, window=1s
        assertThat(rl.tryAcquire(k, 1, 1)).isTrue();
        assertThat(rl.tryAcquire(k, 1, 1)).isFalse();

        clock.advance(Duration.ofMillis(1100));
        assertThat(rl.tryAcquire(k, 1, 1)).isTrue(); // allowed again
    }

    /* ================================================================ *
     * 3)  multi-thread fairness                                         *
     * ================================================================ */
    @Test void concurrentRequestsHonorLimit() throws Exception {
        var rl      = newLimiter();
        String key  = "cmd:baz:user:9";
        int permits = 50, threads = 200;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go    = new CountDownLatch(1);

        ConcurrentLinkedQueue<Boolean> results = new ConcurrentLinkedQueue<>();

        IntStream.range(0, threads).forEach(i -> pool.submit(() -> {
            ready.countDown();
            go.await();
            results.add(rl.tryAcquire(key, permits, 60));
            return null;
        }));

        ready.await(); go.countDown();
        pool.shutdown(); pool.awaitTermination(2, TimeUnit.SECONDS);

        long ok  = results.stream().filter(Boolean::booleanValue).count();
        long bad = results.size() - ok;

        assertThat(ok).isEqualTo(permits);
        assertThat(bad).isEqualTo(threads - permits);
    }

    /* ================================================================ *
     * 4)  isolation between buckets                                     *
     * ================================================================ */
    @Test void differentKeysDoNotAffectEachOther() {
        var rl = newLimiter();

        String a = "cmd:ping:user:1";
        String b = "cmd:ping:user:2";

        // each has its own counter (permits=1)
        assertThat(rl.tryAcquire(a, 1, 60)).isTrue();
        assertThat(rl.tryAcquire(b, 1, 60)).isTrue();

        // second attempt per key must fail individually
        assertThat(rl.tryAcquire(a, 1, 60)).isFalse();
        assertThat(rl.tryAcquire(b, 1, 60)).isFalse();
    }
}
