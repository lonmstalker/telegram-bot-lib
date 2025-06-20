package io.lonmstalker.tgkit.validator.moderation;

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
