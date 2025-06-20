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
package io.lonmstalker.observability;

import io.lonmstalker.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Абстракция над системой сбора метрик. */
public interface MetricsCollector extends AutoCloseable {

  /** Возвращает используемый {@link MeterRegistry}. */
  @NonNull MeterRegistry registry();

  /**
   * Получает таймер с заданным именем и набором тегов.
   *
   * @param name имя метрики
   * @param tags набор тегов
   * @return таймер
   */
  @NonNull Timer timer(@NonNull String name, @NonNull Tags tags);

  /**
   * Получает счётчик с заданным именем и тегами.
   *
   * @param name имя счётчика
   * @param tags набор тегов
   * @return счётчик
   */
  @NonNull Counter counter(@NonNull String name, @NonNull Tags tags);
}
