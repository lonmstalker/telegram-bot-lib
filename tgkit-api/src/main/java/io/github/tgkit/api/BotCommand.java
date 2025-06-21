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
package io.github.tgkit.api;

import io.github.tgkit.api.interceptor.BotInterceptor;
import io.github.tgkit.api.matching.CommandMatch;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Обработчик конкретной команды бота.
 *
 * <p><b>Стабильность:</b> API считается стабильным и совместимым между версиями.
 *
 * @param <T> тип объекта Telegram API, с которым работает обработчик
 */
public interface BotCommand<T> {

  /**
   * Выполняет обработку запроса пользователя.
   *
   * @param request запрос, содержащий данные об обновлении
   * @return {@link BotResponse}, который необходимо отправить пользователю, либо {@code null}, если
   *     ответ не требуется
   */
  @Nullable BotResponse handle(@NonNull BotRequest<T> request);

  /**
   * Тип обрабатываемого запроса.
   *
   * @return тип запроса
   */
  @NonNull BotRequestType type();

  /**
   * Правило сопоставления команды с обновлением.
   *
   * @return правило (matcher)
   */
  @NonNull CommandMatch<T> matcher();

  /**
   * Список интерсепторов команды.
   *
   * @return изменяемый список
   */
  @NonNull List<BotInterceptor> interceptors();

  void setMatcher(@NonNull CommandMatch<T> matcher);

  void setType(@NonNull BotRequestType type);

  void setBotGroup(@NonNull String group);

  /**
   * Группа обработчика. Используется для объединения команд.
   *
   * @return название группы
   */
  default @NonNull String botGroup() {
    return "";
  }

  /**
   * Порядок выполнения команды (меньше — выше приоритет).
   *
   * @return целочисленный порядок
   */
  default int order() {
    return BotCommandOrder.LAST;
  }

  /**
   * Добавляет {@link BotInterceptor} к данной команде.
   *
   * @param interceptor интерсептор, выполняющийся до/после handle()
   */
  default void addInterceptor(@NonNull BotInterceptor interceptor) {
    interceptors().add(interceptor);
  }

  /**
   * Краткое описание команды для help-системы.
   *
   * @return текст описания
   */
  default @NonNull String getDescription() {
    return "";
  }

  /**
   * Пример использования команды.
   *
   * @return текст-образец
   */
  default @NonNull String getUsage() {
    return "";
  }
}
