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

import io.github.tgkit.internal.i18n.MessageKey;
import io.github.tgkit.internal.validator.Validator;
import io.github.tgkit.validator.moderation.ContentModerationService;
import java.util.List;
import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

/**
 * Валидаторы для фотографий (список PhotoSize из Telegram API).
 *
 * <p>Проверяют общий размер, минимальное разрешение и SafeSearch.
 */
public final class PhotoValidators {

  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  private PhotoValidators() {}

  /**
   * Проверяет, что каждая PhotoSize не больше указанного размера в килобайтах.
   *
   * @param maxKb максимальный размер одного фото в килобайтах
   * @return Validator<List < PhotoSize>> с ключом "error.photo.tooLarge"
   */
  public static Validator<@NonNull List<PhotoSize>> maxSizeKb(int maxKb) {
    return Validator.of(
        list ->
            list.stream()
                .allMatch(ps -> ps.getFileSize() != null && ps.getFileSize() <= maxKb * 1024),
        MessageKey.of("error.photo.tooLarge", maxKb));
  }

  /**
   * Проверяет, что каждое фото имеет разрешение не ниже заданного.
   *
   * @param w минимальная ширина в пикселях
   * @param h минимальная высота в пикселях
   * @return Validator<List < PhotoSize>> с ключом "error.photo.resolution"
   */
  public static Validator<@NonNull List<PhotoSize>> minResolution(int w, int h) {
    return Validator.of(
        list -> list.stream().allMatch(ps -> ps.getWidth() >= w && ps.getHeight() >= h),
        MessageKey.of("error.photo.resolution", w, h));
  }

  /**
   * Проверяет, что фото безопасно (не NSFW/violence) через Google Vision SafeSearch.
   *
   * @return Validator<List < PhotoSize>> с ключом "error.photo.unsafe"
   */
  public static Validator<@NonNull List<PhotoSize>> safeSearch() {
    return Validator.of(
        list -> MOD == null || list.stream().allMatch(ps -> MOD.isImageSafe(ps.getFileId())),
        MessageKey.of("error.photo.unsafe"));
  }
}
