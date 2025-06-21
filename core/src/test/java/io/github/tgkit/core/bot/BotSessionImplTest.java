/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.internal.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.github.observability.BotObservability;
import io.github.observability.MetricsCollector;
import io.github.tgkit.internal.config.BotGlobalConfig;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

public class BotSessionImplTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void startAndStop() {
    NoopExecutor executor = new NoopExecutor();
    BotSessionImpl session =
        new BotSessionImpl(executor, null, BotConfig.DEFAULT_UPDATE_QUEUE_CAPACITY);
    DefaultBotOptions options = new DefaultBotOptions();
    session.setOptions(options);
    session.setToken("TOKEN");
    session.setCallback(new DummyBot(options));
    session.start();
    assertTrue(session.isRunning());
    assertThrows(IllegalStateException.class, session::start);
    session.stop();
    assertFalse(session.isRunning());
    assertThrows(IllegalStateException.class, session::stop);
  }

  @Test
  void networkErrorBackoff() {
    BotSessionImpl session = new BotSessionImpl();
    long backOff = session.handleError(new IOException("fail"), 1);
    assertEquals(2, backOff);
    assertEquals(30, session.handleError(new IOException("fail"), 32));
  }

  @Test
  void rejectOnOverflow() throws Exception {
    BotSessionImpl session = new BotSessionImpl(null, null, 2);
    var field = BotSessionImpl.class.getDeclaredField("updates");
    field.setAccessible(true);
    @SuppressWarnings("unchecked")
    var queue = (BlockingQueue<Update>) field.get(session);
    Update u1 = new Update();
    u1.setUpdateId(1);
    Update u2 = new Update();
    u2.setUpdateId(2);
    Update u3 = new Update();
    u3.setUpdateId(3);

    assertTrue(session.enqueueUpdate(u1));
    assertTrue(session.enqueueUpdate(u2));
    assertFalse(session.enqueueUpdate(u3));

    assertEquals(2, queue.size());
    assertTrue(queue.contains(u1));
    assertTrue(queue.contains(u2));
    assertFalse(queue.contains(u3));
  }

  @Test
  void metricsRecordedOnReject() {
    MetricsCollector mc = BotObservability.micrometer(0);
    BotGlobalConfig.INSTANCE.observability().collector(mc);
    BotSessionImpl session = new BotSessionImpl(null, null, 1);
    Update u1 = new Update();
    u1.setUpdateId(1);
    Update u2 = new Update();
    u2.setUpdateId(2);

    assertTrue(session.enqueueUpdate(u1));
    assertFalse(session.enqueueUpdate(u2));

    assertEquals(1.0, mc.registry().find("updates_dropped_total").counter().count());
    assertEquals(1.0, mc.registry().find("updates_queue_size").gauge().value());
  }

  @Test
  void handleErrorLogsStacktrace() {
    var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(BotSessionImpl.class);
    var appender =
        new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);

    BotSessionImpl session = new BotSessionImpl();
    session.handleError(new IOException("boom"), 1);

    logger.detachAppender(appender);
    appender.stop();

    assertFalse(appender.list.isEmpty());
    var event = appender.list.get(0);
    assertEquals(ch.qos.logback.classic.Level.WARN, event.getLevel());
    assertNotNull(event.getThrowableProxy());
  }

  private static class DummyBot implements LongPollingBot {
    private final DefaultBotOptions opt;

    DummyBot(DefaultBotOptions opt) {
      this.opt = opt;
    }

    @Override
    public void onUpdateReceived(Update update) {}

    @Override
    public void onUpdatesReceived(List<Update> updates) {}

    @Override
    public BotOptions getOptions() {
      return opt;
    }

    @Override
    public void clearWebhook() {}

    @Override
    public void onClosing() {}

    @Override
    public String getBotUsername() {
      return "";
    }

    @Override
    public String getBotToken() {
      return "TOKEN";
    }
  }

  private static class NoopExecutor extends AbstractExecutorService {
    private boolean shutdown;

    @Override
    public void shutdown() {
      shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
      shutdown = true;
      return List.of();
    }

    @Override
    public boolean isShutdown() {
      return shutdown;
    }

    @Override
    public boolean isTerminated() {
      return shutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
      return true;
    }

    @Override
    public void execute(Runnable command) {
      /* do nothing */
    }
  }
}
