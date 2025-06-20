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
package io.lonmstalker.tgkit.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Сравнивает текст сообщения с заданной строкой.
 *
 * <p>Пример:
 * <pre>{@code
 * CommandMatch<Message> match = new MessageTextMatch("/start");
 * }
 * </pre>
 */
public class MessageTextMatch implements CommandMatch<Message> {

  private final String text;
  private final boolean ignoreCase;

  public MessageTextMatch(@NonNull String text) {
    this(text, false);
  }

  public MessageTextMatch(@NonNull String text, boolean ignoreCase) {
    this.text = text;
    this.ignoreCase = ignoreCase;
  }

  @Override
  public boolean match(@NonNull Message data) {
    if (data.getText() == null) {
      return false;
    }
    if (ignoreCase) {
      return text.equalsIgnoreCase(data.getText());
    }
    return text.equals(data.getText());
  }
}
