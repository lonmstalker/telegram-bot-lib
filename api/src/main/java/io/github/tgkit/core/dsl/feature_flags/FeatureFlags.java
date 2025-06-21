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
package io.github.tgkit.core.dsl.feature_flags;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Контракт проверки фич-флагов и A/B-тестов. */
public interface FeatureFlags {

  /** Включён ли флаг для чата? */
  boolean isEnabled(@NonNull String key, long chatId);

  /** Включён ли флаг для пользователя? */
  boolean isEnabledForUser(@NonNull String key, long userId);

  /** Вариант (“control”/“variant”) для A/B-теста; null = не участвует. */
  @Nullable Variant variant(@NonNull String abKey, long entityId);

  /** Включить флаг для чата */
  void enableChat(@NonNull String key, long chatId);

  /** Включить флаг для пользователя */
  void enableUser(@NonNull String key, long userId);

  /** Выключить флаг для чата */
  void disableChat(@NonNull String key, long chatId);

  /** Выключить флаг для пользователя */
  void disableUser(@NonNull String key, long userId);

  /** Добавить ключ для A/B */
  void rollout(@NonNull String key, int percent);

  /** Выключить флаг */
  void disable(@NonNull String key);

  /** Включить флаг */
  void enable(@NonNull String key);

  enum Variant {
    CONTROL,
    VARIANT
  }
}
