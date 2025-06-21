package io.github.tgkit.flag.test;

/** Simple API to override feature flags in tests. */
public interface Flags {
  /** Enable feature flag temporarily. */
  void enable(String key);

  /** Disable feature flag temporarily. */
  void disable(String key);
}
