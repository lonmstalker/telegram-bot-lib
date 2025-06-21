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

import io.github.tgkit.core.i18n.MessageKey;
import io.github.tgkit.core.validator.Validator;
import io.github.tgkit.validator.moderation.ContentModerationService;
import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Video;

/**
 * Валидаторы для видеосообщений (Video из Telegram API).
 *
 * <p>Проверяют размер, продолжительность и SafeSearch.
 */
public final class VideoValidators {

  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  private VideoValidators() {}

  /**
   * Проверяет, что размер видео не превышает заданного в килобайтах.
   *
   * @param maxKb максимальный размер в килобайтах
   * @return Validator<Video> с ключом "error.video.tooLarge"
   */
  public static Validator<@NonNull Video> maxSizeKb(long maxKb) {
    return Validator.of(
        v -> v.getFileSize() != null && v.getFileSize() <= maxKb * 1024L,
        MessageKey.of("error.video.tooLarge", maxKb));
  }

  /**
   * Проверяет, что длительность видео не превышает заданную (в секундах).
   *
   * @param maxSec максимальная продолжительность в секундах
   * @return Validator<Video> с ключом "error.video.tooLong"
   */
  public static Validator<@NonNull Video> maxDurationSec(int maxSec) {
    return Validator.of(
        v -> v.getDuration() != null && v.getDuration() <= maxSec,
        MessageKey.of("error.video.tooLong", maxSec));
  }

  /**
   * Проверяет безопасность видео через Cloud-Moderation.
   *
   * @return Validator<Video> с ключом "error.video.unsafe"
   */
  public static Validator<@NonNull Video> safeSearch() {
    return Validator.of(
        v -> MOD == null || MOD.isVideoSafe(v.getFileId()), MessageKey.of("error.video.unsafe"));
  }
}
