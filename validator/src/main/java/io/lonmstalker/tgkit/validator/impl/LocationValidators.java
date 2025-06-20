package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Location;

/**
 * Валидатор для геолокации (Location из Telegram API).
 *
 * <p>Проверяет, что широта и долгота находятся в допустимых пределах.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocationValidators {

  private static final double LAT_MIN = -90.0, LAT_MAX = 90.0;
  private static final double LNG_MIN = -180.0, LNG_MAX = 180.0;

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
