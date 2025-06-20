package io.lonmstalker.tgkit.core.bot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReceiverCloseLoggingTest {

  private final Logger lpLogger = (Logger) LoggerFactory.getLogger(LongPollingReceiver.class);
  private final Logger whLogger = (Logger) LoggerFactory.getLogger(WebHookReceiver.class);
  private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

  static {
    TestBotBootstrap.initOnce();
  }

  @BeforeEach
  void setUp() {
    appender.start();
    lpLogger.addAppender(appender);
    whLogger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    lpLogger.detachAppender(appender);
    whLogger.detachAppender(appender);
    appender.stop();
  }

  @Test
  void longPollingLogsWarningWhenAdapterCloseFails() throws Exception {
    FailingAdapter adapter = new FailingAdapter();
    TelegramSender sender = new TelegramSender(BotConfig.builder().build(), "token");
    LongPollingReceiver receiver =
        new LongPollingReceiver(BotConfig.builder().build(), adapter, "token", sender, null);

    receiver.close();

    assertFalse(appender.list.isEmpty());
    ILoggingEvent event = appender.list.get(0);
    assertEquals(Level.WARN, event.getLevel());
  }

  @Test
  void webHookLogsWarningWhenAdapterCloseFails() {
    FailingAdapter adapter = new FailingAdapter();
    TelegramSender sender = new TelegramSender(BotConfig.builder().build(), "token");
    WebHookReceiver receiver =
        new WebHookReceiver(BotConfig.builder().build(), adapter, "token", sender, null);

    receiver.close();

    assertFalse(appender.list.isEmpty());
    ILoggingEvent event = appender.list.get(appender.list.size() - 1);
    assertEquals(Level.WARN, event.getLevel());
  }

  private static class FailingAdapter implements BotAdapter, AutoCloseable {
    @Override
    public BotApiMethod<?> handle(@NonNull Update update) {
      return null;
    }

    @Override
    public void close() {
      throw new RuntimeException("boom");
    }
  }
}
