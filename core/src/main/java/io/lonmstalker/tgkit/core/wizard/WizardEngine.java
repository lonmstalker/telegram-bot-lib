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

package io.github.tgkit.core.wizard;

import io.github.tgkit.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Диспетчер пошаговых сценариев (wizard). Поддерживает мультисессии, pause/resume,
 * timeout/reminders и ветвления.
 */
public interface WizardEngine {

  /**
   * Регистрирует новый сценарий.
   *
   * @param wizard экземпляр, аннотированный @WizardMeta
   */
  void register(@NonNull Wizard<?> wizard);

  /**
   * Запускает сценарий, создаёт новую сессию и возвращает её sessionId.
   *
   * @param wizardId идентификатор сценария
   * @param request  входящее сообщение
   * @return уникальный sessionId
   */
  @NonNull
  String start(@NonNull String wizardId, @NonNull BotRequest<?> request);

  /**
   * Продолжает работу существующей или создаёт новую сессию, исходя из данных в request (payload
   * содержит sessionId).
   *
   * @param request входящее сообщение (или callback), содержит sessionId
   */
  void route(@NonNull BotRequest<?> request);

  /**
   * Возобновляет ранее приостановленный сценарий (например, по команде /resume).
   *
   * @param request входящее сообщение
   */
  void resume(@NonNull BotRequest<?> request);
}
