package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BotAdapterImplTest {

    @Test
    void shouldInvokeAfterCompletionWhenPreHandleThrowsException() {
        TestInterceptor interceptor = new TestInterceptor();
        BotConfig config = BotConfig.builder()
                .globalInterceptors(List.of(interceptor))
                .build();
        Bot bot = mock(Bot.class);
        when(bot.config()).thenReturn(config);
        BotAdapterImpl adapter = new BotAdapterImpl(
                bot,
                (u, t) -> new org.telegram.telegrambots.meta.api.objects.Message(),
                u -> new DummyUser());

        Update update = new Update();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> adapter.handle(update));

        assertSame(ex, interceptor.error);
        assertTrue(interceptor.afterCompletionCalled);
    }

    private static class DummyUser implements BotUserInfo {
        @Override public @NonNull String chatId() { return "1"; }
        @Override public @NonNull Set<String> roles() { return Set.of(); }
    }

    private static class TestInterceptor implements BotInterceptor {
        boolean afterCompletionCalled;
        Exception error;

        @Override
        public void preHandle(@NonNull Update update) {
            throw new RuntimeException("boom");
        }

        @Override
        public void postHandle(@NonNull Update update) {
            // no-op
        }

        @Override
        public void afterCompletion(@NonNull Update update, BotResponse response, Exception ex) {
            afterCompletionCalled = true;
            error = ex;
        }
    }
}
