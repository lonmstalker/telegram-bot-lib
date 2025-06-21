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
package io.github.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.plugin.BotPlugin;
import io.lonmstalker.tgkit.plugin.BotPluginContext;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public class BotBuilderTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @io.github.tgkit.core.annotation.BotCommand
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
    public io.github.tgkit.core.matching.CommandMatch<BotApiObject> matcher() {
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
