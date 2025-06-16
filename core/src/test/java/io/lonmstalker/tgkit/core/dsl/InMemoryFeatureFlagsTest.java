package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InMemoryFeatureFlagsTest {

    @Test
    void enableChatAndUser() {
        InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
        ff.enableChat("NEW_MENU", 10L);
        ff.enableUser("VIP",      99L);

        assertThat(ff.enabled("NEW_MENU", 10L)).isTrue();
        assertThat(ff.enabledForUser("VIP", 99L)).isTrue();
    }

    @Test
    void percentRollout() {
        InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
        ff.rollout("BETA", 30);             // 30 %

        // deterministic: chatId % 100
        assertThat(ff.enabled("BETA",  5)).isTrue();   // 5 < 30
        assertThat(ff.enabled("BETA", 35)).isFalse();  // 35 ≥ 30
    }
}
