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
package io.github.tgkit.api.user;

import java.util.Locale;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Сводная информация о пользователе Telegram.
 *
 * <p>Используется при авторизации и персонализации, содержит основные идентификаторы и роли
 * пользователя.
 */
public interface BotUserInfo {

  /** Идентификатор чата, если известен. */
  @Nullable Long chatId();

  /** Идентификатор пользователя Telegram. */
  @Nullable Long userId();

  /** Внутренний идентификатор пользователя в системе. */
  @Nullable Long internalUserId();

  /** Набор ролей пользователя. */
  @NonNull Set<String> roles();

  /** Локаль пользователя для выбора языка сообщений. */
  default @Nullable Locale locale() {
    return null;
  }
}
