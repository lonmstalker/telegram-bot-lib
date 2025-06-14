import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.interceptor.BotInterceptor;
import io.lonmstalker.core.bot.*;
import io.lonmstalker.core.storage.BotRequestHolder;
import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("BotAdapterImpl")
class BotAdapterImplTest {
    @Test
    @DisplayName("handle вызывает перехватчики и очищает хранилище")
    void handleShouldInvokeInterceptorsAndClearHolder() {
        BotInterceptor interceptor = mock(BotInterceptor.class);
        BotConfig config = new BotConfig();
        config.setGlobalInterceptors(List.of(interceptor));

        BotCommandRegistry registry = mock(BotCommandRegistry.class);
        BotCommand<Message> command = mock(BotCommand.class);
        when(registry.find(eq(BotRequestType.MESSAGE), any())).thenReturn(command);

        Bot bot = mock(Bot.class);
        when(bot.config()).thenReturn(config);
        when(bot.registry()).thenReturn(registry);
        when(bot.token()).thenReturn("token");

        BotRequestConverter<Message> converter = mock(BotRequestConverter.class);
        BotUserProvider provider = mock(BotUserProvider.class);
        BotUserInfo userInfo = mock(BotUserInfo.class);
        when(provider.resolve(any())).thenReturn(userInfo);

        Update update = new Update();
        Message msg = new Message();
        update.setMessage(msg);
        when(converter.convert(update, BotRequestType.MESSAGE)).thenReturn(msg);

        when(command.handle(any())).thenReturn(new BotResponse());

        BotAdapterImpl adapter = new BotAdapterImpl(bot, (BotRequestConverter) converter, provider);
        adapter.handle(update);

        verify(interceptor).preHandle(update);
        verify(interceptor).postHandle(update);
        verify(interceptor).afterCompletion(eq(update), any());
        assertNull(BotRequestHolder.getUpdate());
        assertNull(BotRequestHolder.getSender());
    }
}
