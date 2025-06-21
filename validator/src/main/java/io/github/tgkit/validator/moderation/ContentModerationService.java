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
package io.github.tgkit.validator.moderation;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Интерфейс для интеграции с внешними Cloud/ML сервисами (Perspective API, Vision SafeSearch, Safe
 * Browsing и т. д.).
 */
public interface ContentModerationService {

  boolean isToxicText(@NonNull String text);

  boolean isProfaneText(@NonNull String text);

  boolean isImageSafe(@NonNull String fileUniqueId);

  boolean isVideoSafe(@NonNull String fileUniqueId);

  boolean isUrlSafe(@NonNull String url);

  /** возвращает true если документ не содержит PII/PCI */
  boolean isDocumentSafe(@NonNull String fileUniqueId, @NonNull String mimeType);
}
