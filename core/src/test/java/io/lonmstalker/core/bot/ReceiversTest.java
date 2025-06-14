import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.LongPollingReceiver;
import io.lonmstalker.core.bot.WebHookReceiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Receivers")
class ReceiversTest {
    @Test
    @DisplayName("LongPollingReceiver исполняет метод")
    void longPollingReceiverShouldExecuteMethod() throws Exception {
        BotApiMethod<Serializable> method = new SendMessage();
        BotAdapter adapter = mock(BotAdapter.class);
        when(adapter.handle(any())).thenReturn(method);
        class TestReceiver extends LongPollingReceiver {
            BotApiMethod<?> executed;
            TestReceiver() { super(new BotConfig(), adapter, "t", null); }
            @Override
            public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method m) { executed = m; return null; }
        }
        TestReceiver r = new TestReceiver();
        r.onUpdateReceived(new Update());
        assertEquals(method, r.executed);
    }

    @Test
    @DisplayName("WebHookReceiver обрабатывает ошибки")
    void webHookReceiverHandlesErrors() {
        BotAdapter adapter = mock(BotAdapter.class);
        when(adapter.handle(any())).thenThrow(new RuntimeException("err"));
        var receiver = new WebHookReceiver(new BotConfig(), adapter, "t", (u,e) -> assertNotNull(e));
        assertNull(receiver.onWebhookUpdateReceived(new Update()));
    }
}
