package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.BotInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InlineResultsTest {

    @Test
    void resultsListContainsAddedItems() {
        BotInfo bot = mock(BotInfo.class);
        BotUserInfo user = mock(BotUserInfo.class);
        when(user.userId()).thenReturn(1L);
        DSLContext ctx = new DSLContext.SimpleDSLContext(bot, user);

        InlineResults res = new InlineResults(ctx)
                .article("1", "title", "hello")
                .photo("2", "https://img", "https://thumb");

        assertThat(res.results()).hasSize(2);
    }
}
