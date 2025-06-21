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
import java.net.URI;
import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Валидаторы для URL (WebAppData или ссылки).
 *
 * <p>Проверяют синтаксис URI и безопасность через Safe Browsing.
 */
public final class UrlValidators {

  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  private UrlValidators() {}

  /**
   * Проверяет, что строка является корректным URI.
   *
   * @return Validator<String> с ключом "error.url.syntax"
   */
  public static Validator<@NonNull String> validUri() {
    return Validator.of(
        s -> {
          try {
            URI.create(s);
            return true;
          } catch (Exception e) {
            return false;
          }
        },
        MessageKey.of("error.url.syntax"));
  }

  /**
   * Проверяет безопасность URL через Google Safe Browsing.
   *
   * @return Validator<String> с ключом "error.url.unsafe"
   */
  public static Validator<@NonNull String> safeBrowsing() {
    return Validator.of(s -> MOD == null || MOD.isUrlSafe(s), MessageKey.of("error.url.unsafe"));
  }
}
