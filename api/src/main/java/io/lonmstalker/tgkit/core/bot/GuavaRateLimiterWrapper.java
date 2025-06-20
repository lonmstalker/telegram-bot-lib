package io.lonmstalker.tgkit.core.bot;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Обёртка над {@link RateLimiter} для {@link TelegramSender}.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * GuavaRateLimiterWrapper limiter = new GuavaRateLimiterWrapper(10);
 * limiter.acquire();
 * }</pre>
 */
final class GuavaRateLimiterWrapper {

    private final RateLimiter delegate;

    GuavaRateLimiterWrapper(int permitsPerSecond) {
        this.delegate = RateLimiter.create(permitsPerSecond);
    }

    void acquire() {
        delegate.acquire();
    }

    void close() {
        // no resources to release
    }
}
