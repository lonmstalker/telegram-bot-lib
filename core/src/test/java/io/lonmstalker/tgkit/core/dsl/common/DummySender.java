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
package io.lonmstalker.tgkit.core.dsl.common;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

// тестовый BotSender, чтобы не ходить в Telegram
public class DummySender extends TelegramSender {
  public int callCount = 0;
  public PartialBotApiMethod<?> last;

  public DummySender() {
    super(BotConfig.builder().build(), "");
  }

  @Override
  public <T extends Serializable> T execute(PartialBotApiMethod<T> m) {
    callCount++;
    last = m;
    return null; // для send() нам не важен ответ
  }
}
