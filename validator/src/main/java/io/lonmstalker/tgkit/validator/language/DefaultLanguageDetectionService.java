package io.lonmstalker.tgkit.validator.language;

import com.google.common.base.Optional;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Дефолтная реализация {@link LanguageDetectionService} на базе Apache Tika. */
public class DefaultLanguageDetectionService implements LanguageDetectionService {

  private final LanguageDetector detector;

  public DefaultLanguageDetectionService() {
    try {
      this.detector =
          LanguageDetectorBuilder.create(NgramExtractors.standard())
              .withProfiles(new LanguageProfileReader().readAllBuiltIn())
              .build();
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось инициализировать детектор языка", e);
    }
  }

  @Override
  public @NonNull String detect(@Nullable String text) {
    if (text == null || text.isBlank()) {
      return "und";
    }
    // сначала пытаем «простое» детектирование
    Optional<LdLocale> primary = detector.detect(text);
    if (primary.isPresent()) {
      return primary.get().getLanguage();
    }
    // fallback: берём самый вероятный из всех детектированных
    List<DetectedLanguage> all = detector.getProbabilities(text);
    return all.stream()
        .filter(r -> !"und".equals(r.getLocale().getLanguage()))
        .max(Comparator.comparingDouble(DetectedLanguage::getProbability))
        .map(r -> r.getLocale().getLanguage())
        .orElse("und");
  }
}
