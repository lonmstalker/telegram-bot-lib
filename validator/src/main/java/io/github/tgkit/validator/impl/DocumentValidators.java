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

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import io.github.tgkit.validator.moderation.ContentModerationService;
import java.util.ServiceLoader;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Document;

/**
 * Валидаторы для документов (Document из Telegram API).
 *
 * <p>Проверяют размер, MIME-тип и DLP/Cloud-модерацию содержимого.
 */
public final class DocumentValidators {

  private DocumentValidators() {}

  private static final Set<String> ALLOWED_MIME =
      Set.of(
          "application/pdf",
          "application/zip",
          "application/msword",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  /**
   * Проверяет, что размер документа не больше заданного (в мегабайтах).
   *
   * @param maxMb максимальный размер в мегабайтах
   * @return Validator<Document> с ключом "error.doc.tooLarge"
   */
  public static Validator<@NonNull Document> maxSizeMb(int maxMb) {
    long maxBytes = (long) maxMb * 1024 * 1024;
    return Validator.of(
        d -> d.getFileSize() != null && d.getFileSize() <= maxBytes,
        MessageKey.of("error.doc.tooLarge", maxMb));
  }

  /**
   * Проверяет, что MIME-тип документа разрешён.
   *
   * @return Validator<Document> с ключом "error.doc.mime"
   */
  public static Validator<@NonNull Document> allowedMime() {
    return Validator.of(
        d -> d.getMimeType() != null && ALLOWED_MIME.contains(d.getMimeType()),
        MessageKey.of("error.doc.mime"));
  }

  /**
   * Проверяет содержимое документа на отсутствие PII/PCI через DLP.
   *
   * @return Validator<Document> с ключом "error.doc.unsafe"
   */
  public static Validator<@NonNull Document> safeContent() {
    return Validator.of(
        d -> MOD == null || MOD.isDocumentSafe(d.getFileId(), d.getMimeType()),
        MessageKey.of("error.doc.unsafe"));
  }
}
