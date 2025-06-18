package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InMemoryFeatureFlagsTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void enableChatAndUser() {
        InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
        ff.enableChat("NEW_MENU", 10L);
        ff.enableUser("VIP",      99L);

        assertThat(ff.isEnabled("NEW_MENU", 10L)).isTrue();
        assertThat(ff.isEnabledForUser("VIP", 99L)).isTrue();
    }

    @Test
    void percentRollout() {
        InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
        ff.rollout("BETA", 30);             // 30 %

        // deterministic: chatId % 100
        assertThat(ff.isEnabled("BETA",  5)).isTrue();   // 5 < 30
        assertThat(ff.isEnabled("BETA", 35)).isFalse();  // 35 ≥ 30
    }
}
