package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.validator.Validator;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.validator.moderation.ContentModerationService;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.util.Set;
import java.util.ServiceLoader;

/**
 * Валидаторы для документов (Document из Telegram API).
 * <p>
 * Проверяют размер, MIME-тип и DLP/Cloud-модерацию содержимого.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentValidators {

    private static final Set<String> ALLOWED_MIME = Set.of(
            "application/pdf",
            "application/zip",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final ContentModerationService MOD =
            ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

    /**
     * Проверяет, что размер документа не больше заданного (в мегабайтах).
     *
     * @param maxMb максимальный размер в мегабайтах
     * @return Validator<Document> с ключом "error.doc.tooLarge"
     */
    public static Validator<@NonNull Document> maxSizeMb(int maxMb) {
        long maxBytes = (long)maxMb * 1024 * 1024;
        return Validator.of(
                d -> d.getFileSize() != null && d.getFileSize() <= maxBytes,
                MessageKey.of("error.doc.tooLarge", maxMb)
        );
    }

    /**
     * Проверяет, что MIME-тип документа разрешён.
     *
     * @return Validator<Document> с ключом "error.doc.mime"
     */
    public static Validator<@NonNull Document> allowedMime() {
        return Validator.of(
                d -> d.getMimeType() != null && ALLOWED_MIME.contains(d.getMimeType()),
                MessageKey.of("error.doc.mime")
        );
    }

    /**
     * Проверяет содержимое документа на отсутствие PII/PCI через DLP.
     *
     * @return Validator<Document> с ключом "error.doc.unsafe"
     */
    public static Validator<@NonNull Document> safeContent() {
        return Validator.of(
                d -> MOD == null || MOD.isDocumentSafe(d.getFileId(), d.getMimeType()),
                MessageKey.of("error.doc.unsafe")
        );
    }
}