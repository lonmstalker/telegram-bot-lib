package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;

class BotImplActionTimeoutTest {

  static {
    TestBotBootstrap.initOnce();
  }

  private DummySender sender;

  @BeforeEach
  void setUp() {
    sender = new DummySender(new DefaultBotOptions());
  }

  @Test
  void defaultTimeoutFromConstant() {
    BotConfig cfg = BotConfig.builder().build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .build();
    assertEquals(BotConfig.DEFAULT_ON_COMPLETED_ACTION_TIMEOUT_MS, bot.getOnCompletedActionTimeoutMs());
  }

  @Test
  void timeoutFromConfig() {
    BotConfig cfg = BotConfig.builder().onCompletedActionTimeoutMs(12345L).build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .build();
    assertEquals(12345L, bot.getOnCompletedActionTimeoutMs());
  }

  @Test
  void builderOverridesConfig() {
    BotConfig cfg = BotConfig.builder().onCompletedActionTimeoutMs(5000L).build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .onCompletedActionTimeoutMs(20000L)
            .build();
    assertEquals(20000L, bot.getOnCompletedActionTimeoutMs());
  }

  private static final class DummySender extends DefaultAbsSender {
    DummySender(DefaultBotOptions opt) {
      super(opt, "TOKEN");
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
      if (method instanceof GetMe) {
        User u = new User();
        u.setId(42L);
        u.setUserName("tester");
        return (T) u;
      }
      return null;
    }

    @Override
    public void close() {}
  }
}

