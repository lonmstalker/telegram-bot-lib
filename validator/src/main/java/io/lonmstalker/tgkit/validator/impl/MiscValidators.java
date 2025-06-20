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
package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

/**
 * Бизнес-валидации для Telegram-объектов, рассчитанные на проверку содержательных правил:
 * разрешённых наборов, максимальных продолжительностей, максимального размера и т.п.
 *
 * <p>Предполагается, что fileId всегда приходит от Telegram, поэтому его наличие не проверяется
 * здесь.
 */
public final class MiscValidators {

  private MiscValidators() {}

  /**
   * Стикер должен принадлежать одному из разрешённых наборов.
   *
   * @param allowedSets набор имён допустимых sticker_set_name
   */
  public static Validator<@NonNull Sticker> allowedStickerSets(@NonNull Set<String> allowedSets) {
    Objects.requireNonNull(allowedSets, "allowedSets");
    return Validator.of(
        sticker -> sticker.getSetName() != null && allowedSets.contains(sticker.getSetName()),
        MessageKey.of("error.sticker.notAllowedSet"));
  }

  /**
   * Анимация (GIF) не должна превышать указанной продолжительности.
   *
   * @param maxDurationSec максимально допустимая длина анимации в секундах
   */
  public static Validator<@NonNull Animation> maxAnimationDuration(int maxDurationSec) {
    return Validator.of(
        anim -> anim.getDuration() <= maxDurationSec,
        MessageKey.of("error.animation.tooLong", maxDurationSec));
  }

  /**
   * Игра должна иметь короткое имя из списка разрешённых.
   *
   * @param allowedShortNames множество допустимых game_short_name
   */
  public static Validator<@NonNull Game> allowedGameShortNames(
      @NonNull Set<String> allowedShortNames) {
    Objects.requireNonNull(allowedShortNames, "allowedShortNames");
    return Validator.of(
        game -> allowedShortNames.contains(game.getTitle()),
        MessageKey.of("error.game.notAllowed"));
  }

  /**
   * Видео-заметка (video_note) не должна превышать указанной длительности.
   *
   * @param maxDurationSec максимально допустимая длительность видео-заметки
   */
  public static Validator<@NonNull VideoNote> maxVideoNoteDuration(int maxDurationSec) {
    return Validator.of(
        vn -> vn.getDuration() != null && vn.getDuration() <= maxDurationSec,
        MessageKey.of("error.videonote.tooLong", maxDurationSec));
  }

  /**
   * Голосовое сообщение (voice) не должно превышать указанной длительности.
   *
   * @param maxDurationSec максимально допустимая длительность в секундах
   */
  public static Validator<@NonNull Voice> maxVoiceDuration(int maxDurationSec) {
    return Validator.of(
        voice -> voice.getDuration() != null && voice.getDuration() <= maxDurationSec,
        MessageKey.of("error.voice.tooLong", maxDurationSec));
  }
}
