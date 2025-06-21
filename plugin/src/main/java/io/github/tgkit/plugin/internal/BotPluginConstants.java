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
package io.github.tgkit.plugin;

/**
 * Набор констант, используемых системой плагинов.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * if (descriptor.api().compareTo(BotPluginConstants.CURRENT_VERSION) > 0) {
 *     throw new UnsupportedOperationException();
 * }
 * }
 * </pre>
 */
public class BotPluginConstants {
  /** Текущая поддерживаемая версия API плагинов. */
  public static final String CURRENT_VERSION = "0.1";

  private BotPluginConstants() {}
}
