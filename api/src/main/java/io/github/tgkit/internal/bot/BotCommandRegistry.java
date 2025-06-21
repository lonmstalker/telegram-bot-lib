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

import io.github.tgkit.internal.BotCommand;
import io.github.tgkit.internal.BotRequestType;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface BotCommandRegistry {

  /**
   * Возвращает список всех зарегистрированных команд (неизменяемый).
   *
   * @return список команд
   */
  @NonNull List<BotCommand<?>> all();

  /**
   * Регистрирует новую команду и сортирует список по приоритету.
   *
   * @param command экземпляр команды
   */
  void add(@NonNull BotCommand<?> command);

  /**
   * Ищет первую команду, подходящую под тип, группу и matcher.
   *
   * @param type тип запроса
   * @param botGroup группа команд (любая == пустая строка)
   * @param data объект Telegram API
   * @param <T> тип объекта
   * @return команда или {@code null}, если не найдена
   */
  <T extends BotApiObject> @Nullable BotCommand<T> find(
      @NonNull BotRequestType type, @NonNull String botGroup, @NonNull T data);
}
