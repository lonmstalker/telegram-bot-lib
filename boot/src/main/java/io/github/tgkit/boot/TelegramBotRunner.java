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
package io.github.tgkit.boot;

import io.github.tgkit.internal.bot.Bot;
import io.github.tgkit.internal.bot.BotAdapter;
import io.github.tgkit.internal.bot.BotAdapterImpl;
import io.github.tgkit.internal.bot.BotConfig;
import io.github.tgkit.internal.bot.BotFactory;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.init.BotCoreInitializer;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/** Компонент для запуска Telegram-бота при старте Spring Boot приложения. */
public final class TelegramBotRunner implements ApplicationRunner {

  private final BotProperties props;
  private @Nullable Bot bot;

  public TelegramBotRunner(BotProperties props) {
    this.props = props;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    BotCoreInitializer.init();

    BotConfig cfg =
        BotConfig.builder()
            .baseUrl(props.getBaseUrl())
            .botGroup(props.getBotGroup())
            .requestsPerSecond(props.getRequestsPerSecond())
            .build();

    TelegramSender sender = new TelegramSender(cfg, props.getToken());
    BotAdapter adapter = BotAdapterImpl.builder().sender(sender).config(cfg).build();
    List<String> pkgs = props.getPackages();
    bot = BotFactory.INSTANCE.from(props.getToken(), cfg, adapter, pkgs.toArray(new String[0]));
    bot.start();
  }

  /** Останавливает запущенного бота. */
  public void stop() {
    if (bot != null) {
      bot.stop();
    }
  }

  /** Возвращает запущенный экземпляр бота или {@code null}. */
  public @Nullable Bot bot() {
    return bot;
  }
}
