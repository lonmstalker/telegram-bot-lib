package io.lonmstalker.tgkit.validator.language;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ServiceLoader;

/**
 * Сервис для определения языка произвольного текста.
 * <p>
 * Использует ServiceLoader для загрузки пользовательских
 * реализаций. Если ни одна не найдена, возвращает
 * {@link DefaultLanguageDetectionService}.
 */
public interface LanguageDetectionService {

    /**
     * Возвращает активный экземпляр {@code LanguageDetectionService}.
     *
     * @return реализация сервиса определения языка
     */
    static LanguageDetectionService get() {
        return ServiceLoader.load(LanguageDetectionService.class)
                .findFirst()
                .orElseGet(DefaultLanguageDetectionService::new);
    }

    /**
     * Определяет язык текста и возвращает ISO-639-1 код (двухбуквенный).
     * При неудаче возвращает "und" (undetermined).
     *
     * @param text текст для анализа (не null)
     * @return двухбуквенный код языка или "und"
     */
    @NonNull String detect(@NonNull String text);
}
