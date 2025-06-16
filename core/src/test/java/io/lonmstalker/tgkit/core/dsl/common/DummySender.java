package io.lonmstalker.tgkit.core.dsl.common;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;

// тестовый BotSender, чтобы не ходить в Telegram
public class DummySender extends TelegramSender {
    public int callCount = 0;
    public PartialBotApiMethod<?> last;

    public DummySender() {
        super(BotConfig.builder().build(), "");
    }

    @Override public <T extends Serializable> T execute(PartialBotApiMethod<T> m) {
        callCount++;
        last = m;
        return null;                      // для send() нам не важен ответ
    }
}
