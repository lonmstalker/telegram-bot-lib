package io.github.tgkit.flag.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.internal.config.BotGlobalConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Integration test for {@link FlagOverrideExtension}. */
@ExtendWith(FlagOverrideExtension.class)
class FlagOverrideExtensionTest {

  @Test
  void extensionOverridesFlags(Flags flags) {
    flags.enable("X");
    boolean enabled =
        BotGlobalConfig.INSTANCE.dsl().getFeatureFlags().isEnabled("X", 1L);
    assertThat(enabled).isTrue();
  }
}
