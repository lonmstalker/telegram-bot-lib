package io.github.tgkit.flag.test;

import io.github.tgkit.internal.dsl.feature_flags.FeatureFlags;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Feature flag implementation for tests. Supports overriding flag values and
 * collecting branch coverage.
 */
public final class FlagOverrideRegistry implements FeatureFlags, Flags {
  private final FeatureFlags delegate;
  private final Map<String, Boolean> overrides = new ConcurrentHashMap<>();
  private final Map<String, BranchCounter> coverage = new ConcurrentHashMap<>();

  /**
   * Wraps the given {@link FeatureFlags} instance.
   *
   * @param delegate real flags store
   */
  public FlagOverrideRegistry(@NonNull FeatureFlags delegate) {
    this.delegate = delegate;
  }

  /** Returns collected coverage counters. */
  public @NonNull Map<String, BranchCounter> coverage() {
    return coverage;
  }

  /** Clears all overrides and counters. */
  public void reset() {
    overrides.clear();
    coverage.clear();
  }

  FeatureFlags original() {
    return delegate;
  }

  @Override
  public void enable(String key) {
    overrides.put(key, Boolean.TRUE);
  }

  @Override
  public void disable(String key) {
    overrides.put(key, Boolean.FALSE);
  }

  private boolean resolve(String key, boolean actual) {
    Boolean override = overrides.get(key);
    boolean result = override != null ? override : actual;
    coverage.computeIfAbsent(key, k -> new BranchCounter()).hit(result);
    return result;
  }

  @Override
  public boolean isEnabled(@NonNull String key, long chatId) {
    return resolve(key, delegate.isEnabled(key, chatId));
  }

  @Override
  public boolean isEnabledForUser(@NonNull String key, long userId) {
    return resolve(key, delegate.isEnabledForUser(key, userId));
  }

  @Override
  public @NonNull Variant variant(@NonNull String abKey, long entityId) {
    Variant v = delegate.variant(abKey, entityId);
    boolean res = v == Variant.VARIANT;
    coverage.computeIfAbsent(abKey, k -> new BranchCounter()).hit(res);
    return v;
  }

  // Delegated modification methods -----------------------------------------
  @Override
  public void enableChat(@NonNull String key, long chatId) {
    delegate.enableChat(key, chatId);
  }

  @Override
  public void enableUser(@NonNull String key, long userId) {
    delegate.enableUser(key, userId);
  }

  @Override
  public void disableChat(@NonNull String key, long chatId) {
    delegate.disableChat(key, chatId);
  }

  @Override
  public void disableUser(@NonNull String key, long userId) {
    delegate.disableUser(key, userId);
  }

  @Override
  public void rollout(@NonNull String key, int percent) {
    delegate.rollout(key, percent);
  }

  @Override
  public void disable(@NonNull String key) {
    delegate.disable(key);
  }

  @Override
  public void enable(@NonNull String key) {
    delegate.enable(key);
  }

  /** Counter for taken/not-taken branches. */
  public static final class BranchCounter {
    private final AtomicInteger taken = new AtomicInteger();
    private final AtomicInteger skipped = new AtomicInteger();

    void hit(boolean takenBranch) {
      if (takenBranch) {
        taken.incrementAndGet();
      } else {
        skipped.incrementAndGet();
      }
    }

    public int taken() {
      return taken.get();
    }

    public int skipped() {
      return skipped.get();
    }
  }
}
