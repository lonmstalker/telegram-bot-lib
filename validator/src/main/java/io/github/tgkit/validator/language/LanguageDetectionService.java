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

import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Сервис для определения языка произвольного текста.
 *
 * <p>Использует ServiceLoader для загрузки пользовательских реализаций. Если ни одна не найдена,
 * возвращает {@link DefaultLanguageDetectionService}.
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
   * Определяет язык текста и возвращает ISO-639-1 код (двухбуквенный). При неудаче возвращает "und"
   * (undetermined).
   *
   * @param text текст для анализа (не null)
   * @return двухбуквенный код языка или "und"
   */
  @NonNull String detect(@NonNull String text);
}
