package io.lonmstalker.tgkit.core.bot;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Simple wrapper around Guava {@link RateLimiter} to control request throughput.
 */
final class GuavaRateLimiterWrapper implements AutoCloseable {

  private final RateLimiter delegate;

  GuavaRateLimiterWrapper(int permitsPerSecond) {
    this.delegate = RateLimiter.create(permitsPerSecond);
  }

  void acquire() {
    delegate.acquire();
  }

  @Override
  public void close() {
    // nothing to close
  }
}
