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
import io.lonmstalker.tgkit.validator.moderation.ContentModerationService;
import java.util.ServiceLoader;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Валидаторы для текстовых сообщений.
 *
 * <p>Содержат проверки на пустоту, максимальную длину, корректное UTF-8 кодирование и опциональную
 * Cloud-модерацию (toxic/profanity).
 */
public final class TextValidators {

  private TextValidators() {}

  private static final int MAX_LEN = 4096;
  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  /** Валидатор по регулярному выражению. */
  public static @NonNull Validator<String> regex(@NonNull Pattern pattern) {
    return Validator.of(
        s -> s != null && pattern.matcher(s).matches(), MessageKey.of("error.invalidRegex"));
  }

  /** Валидатор диапазона для целых чисел. */
  public static @NonNull Validator<Integer> range(int min, int max) {
    return Validator.of(
        i -> i != null && i >= min && i <= max, MessageKey.of("error.invalidRange", min, max));
  }

  /**
   * Проверяет, что строка не null и не состоит только из пробельных символов.
   *
   * @return Validator<String> с ключом ошибки "error.text.blank"
   */
  public static Validator<@NonNull String> notBlank() {
    return Validator.of(s -> !s.isBlank(), MessageKey.of("error.text.blank"));
  }

  /**
   * Проверяет, что длина строки не превышает {@value MAX_LEN} символов.
   *
   * @return Validator<String> с ключом ошибки "error.text.tooLong"
   */
  public static Validator<@NonNull String> maxLength() {
    return maxLength(MAX_LEN);
  }

  /**
   * Проверяет, что длина строки не превышает {@param length} символов.
   *
   * @return Validator<String> с ключом ошибки "error.text.tooLong"
   */
  public static Validator<@NonNull String> maxLength(int length) {
    return Validator.of(s -> s.length() <= length, MessageKey.of("error.text.tooLong", length));
  }

  /**
   * Проверяет отсутствие токсичного контента через внешний сервис Perspective API.
   *
   * @return Validator<String> с ключом ошибки "error.text.toxic"
   */
  public static Validator<@NonNull String> noToxicity() {
    return Validator.of(s -> MOD == null || !MOD.isToxicText(s), MessageKey.of("error.text.toxic"));
  }

  /**
   * Проверяет отсутствие нецензурной лексики через внешний сервис.
   *
   * @return Validator<String> с ключом ошибки "error.text.profanity"
   */
  public static Validator<@NonNull String> noProfanity() {
    return Validator.of(
        s -> MOD == null || !MOD.isProfaneText(s), MessageKey.of("error.text.profanity"));
  }
}
