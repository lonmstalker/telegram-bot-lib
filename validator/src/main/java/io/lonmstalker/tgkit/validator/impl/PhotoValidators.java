package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import io.lonmstalker.tgkit.validator.moderation.ContentModerationService;
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

  private PhotoValidators() {}

  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  /**
   * Проверяет, что каждая PhotoSize не больше указанного размера в килобайтах.
   *
   * @param maxKb максимальный размер одного фото в килобайтах
   * @return Validator<List<PhotoSize>> с ключом "error.photo.tooLarge"
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
   * @return Validator<List<PhotoSize>> с ключом "error.photo.resolution"
   */
  public static Validator<@NonNull List<PhotoSize>> minResolution(int w, int h) {
    return Validator.of(
        list -> list.stream().allMatch(ps -> ps.getWidth() >= w && ps.getHeight() >= h),
        MessageKey.of("error.photo.resolution", w, h));
  }

  /**
   * Проверяет, что фото безопасно (не NSFW/violence) через Google Vision SafeSearch.
   *
   * @return Validator<List<PhotoSize>> с ключом "error.photo.unsafe"
   */
  public static Validator<@NonNull List<PhotoSize>> safeSearch() {
    return Validator.of(
        list -> MOD == null || list.stream().allMatch(ps -> MOD.isImageSafe(ps.getFileId())),
        MessageKey.of("error.photo.unsafe"));
  }
}
