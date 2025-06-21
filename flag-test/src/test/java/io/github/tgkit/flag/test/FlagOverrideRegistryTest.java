package io.github.tgkit.flag.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.internal.dsl.feature_flags.FeatureFlags;
import io.github.tgkit.internal.dsl.feature_flags.InMemoryFeatureFlags;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link FlagOverrideRegistry}. */
class FlagOverrideRegistryTest {

  @Test
  void overrideTakesPrecedenceAndCountsBranches() {
    InMemoryFeatureFlags base = new InMemoryFeatureFlags();
    base.disable("A");
    FlagOverrideRegistry registry = new FlagOverrideRegistry(base);

    // override to enabled
    registry.enable("A");
    boolean first = registry.isEnabled("A", 1L);
    registry.disable("A");
    boolean second = registry.isEnabled("A", 1L);

    assertThat(first).isTrue();
    assertThat(second).isFalse();
    FlagOverrideRegistry.BranchCounter counter = registry.coverage().get("A");
    assertThat(counter.taken()).isEqualTo(1);
    assertThat(counter.skipped()).isEqualTo(1);
  }

  @Test
  void delegatesVariantCheck() {
    InMemoryFeatureFlags base = new InMemoryFeatureFlags();
    base.rollout("B", 100); // always variant
    FlagOverrideRegistry registry = new FlagOverrideRegistry(base);

    FeatureFlags.Variant v = registry.variant("B", 1L);

    assertThat(v).isEqualTo(FeatureFlags.Variant.VARIANT);
    FlagOverrideRegistry.BranchCounter counter = registry.coverage().get("B");
    assertThat(counter.taken()).isEqualTo(1);
    assertThat(counter.skipped()).isZero();
  }
}
