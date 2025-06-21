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
package io.github.tgkit.api.security.captcha;

import io.github.tgkit.api.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Провайдер CAPTCHA. Возвращает готовое сообщение с вопросом и клавиатурой и проверяет ответ
 * пользователя.
 */
public interface CaptchaProvider {

  /**
   * Формирует сообщение-вопрос для указанного чата.
   *
   * @return {@link PartialBotApiMethod}
   */
  @NonNull PartialBotApiMethod<?> question(@NonNull BotRequest<?> request);

  /**
   * Проверяет ответ пользователя.
   *
   * @param answer ответ пользователя
   * @return {@code true}, если ответ верен
   */
  boolean verify(@NonNull BotRequest<?> request, @NonNull String answer);
}
