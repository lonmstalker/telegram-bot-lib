package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Проверка билдера инлайн-результатов. */
public class InlineResultTest {
    @Test
    void buildResults() {
        InlineResults res = BotResponse.inline(TestUtils.request(1)).article("1", "T", "Text");
        assertThat(res.results()).isNotEmpty();
    }
}
