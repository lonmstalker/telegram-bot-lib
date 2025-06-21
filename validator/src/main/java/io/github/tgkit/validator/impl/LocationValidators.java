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
package io.github.tgkit.validator.impl;

import io.github.tgkit.api.i18n.MessageKey;
import io.github.tgkit.api.validator.Validator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Location;

/**
 * Валидатор для геолокации (Location из Telegram API).
 *
 * <p>Проверяет, что широта и долгота находятся в допустимых пределах.
 */
public final class LocationValidators {

  private static final double LAT_MIN = -90.0, LAT_MAX = 90.0;
  private static final double LNG_MIN = -180.0, LNG_MAX = 180.0;

  private LocationValidators() {}

  /**
   * Проверяет, что координаты лежат в диапазоне [-90..90]×[-180..180].
   *
   * @return Validator<Location> с ключом "error.location.bounds"
   */
  public static Validator<@NonNull Location> inBounds() {
    return Validator.of(
        loc ->
            loc.getLatitude() >= LAT_MIN
                && loc.getLatitude() <= LAT_MAX
                && loc.getLongitude() >= LNG_MIN
                && loc.getLongitude() <= LNG_MAX,
        MessageKey.of("error.location.bounds"));
  }
}
