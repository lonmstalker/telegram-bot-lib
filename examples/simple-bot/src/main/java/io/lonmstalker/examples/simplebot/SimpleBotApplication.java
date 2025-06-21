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
package io.lonmstalker.examples.simplebot;

import io.github.tgkit.core.TelegramBot;
import io.github.tgkit.core.init.BotCoreInitializer;
import java.nio.file.Path;

public class SimpleBotApplication {

  public static void main(String[] args) {
    BotCoreInitializer.init();
    TelegramBot.run(Path.of("bot.yaml"));
  }
}
