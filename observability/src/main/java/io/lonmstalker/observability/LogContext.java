/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.observability;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.MDC;

/** Утилитарный класс для работы с MDC при логировании. */
public final class LogContext {

  private LogContext() {}

  /**
   * Помещает значение в контекст логирования.
   *
   * @param key ключ MDC
   * @param value значение
   */
  public static void put(@NonNull String key, @NonNull String value) {
    MDC.put(key, value);
  }

  /** Очищает MDC. */
  public static void clear() {
    MDC.clear();
  }

  /** Удаляет значение из MDC. */
  public static void remove(@NonNull String key) {
    MDC.remove(key);
  }
}
