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
package io.github.tgkit.validator.language;

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
