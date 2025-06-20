package io.lonmstalker.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.plugin.BotPlugin;
import io.lonmstalker.tgkit.plugin.BotPluginContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public class BotBuilderTest {

  static {
    BotCoreInitializer.init();
  }

  @io.lonmstalker.tgkit.core.annotation.BotCommand
  public static class TestCommand implements BotCommand<BotApiObject> {
    @Override
    public BotResponse handle(BotRequest<BotApiObject> request) {
      return null;
    }

    @Override
    public BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public io.lonmstalker.tgkit.core.matching.CommandMatch<BotApiObject> matcher() {
      return u -> true;
    }
  }

  @io.lonmstalker.tgkit.plugin.annotation.BotPlugin
  public static class TestPlugin implements BotPlugin {
    static final AtomicBoolean started = new AtomicBoolean();

    @Override
    public void onLoad(BotPluginContext ctx) {}

    @Override
    public void start() {
      started.set(true);
    }
  }

  @Test
  void startRegistersCommandAndPlugin() {
    BotBuilder.BotBuilderImpl builder =
        BotBuilder.builder().token("T").withPolling().scan(TestCommand.class.getPackageName());

    Bot bot = builder.start();

    assertNotNull(bot.registry().find(BotRequestType.MESSAGE, "", new BotApiObject() {}));
    assertTrue(TestPlugin.started.get());
  }

  @Test
  void startIsIdempotent() {
    BotBuilder.BotBuilderImpl builder = BotBuilder.builder().token("T").withPolling();
    builder.start();
    assertThrows(IllegalStateException.class, builder::start);
  }
}
