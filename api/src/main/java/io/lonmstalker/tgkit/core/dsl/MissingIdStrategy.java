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

package io.github.tgkit.core.dsl;

import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.core.storage.BotRequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionalInterface
@SuppressWarnings("argument")
public interface MissingIdStrategy {
  Logger LOG = LoggerFactory.getLogger(MissingIdStrategy.class);
  /**
   * --- Готовые стратегии ---
   */
  MissingIdStrategy ERROR =
      (name, u) -> {
        throw new BotApiException(
            name + " is required but null in update: " + BotRequestContextHolder.getRequestId());
      };
  MissingIdStrategy WARN =
      (name, u) ->
          LOG.warn("{} is null in update {}", name, BotRequestContextHolder.getRequestId());
  MissingIdStrategy IGNORE =
      (name, u) -> {
        /* ничего */
      };

  /**
   * Вызывается, когда chatId или userId отсутствуют.
   */
  void onMissing(String idName, DSLContext ctx) throws BotApiException;
}
