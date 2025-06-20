package io.lonmstalker.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

public class BotSessionImplTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void startAndStop() {
    NoopExecutor executor = new NoopExecutor();
    BotSessionImpl session = new BotSessionImpl(executor, new ObjectMapper());
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
