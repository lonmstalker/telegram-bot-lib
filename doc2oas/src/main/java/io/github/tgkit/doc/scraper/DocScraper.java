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
package io.github.tgkit.doc.scraper;

import java.io.InputStream;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Интерфейс парсера HTML-документации Telegram API.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * DocScraper scraper = new JsoupDocScraper();
 * List<MethodDoc> docs = scraper.scrape(stream);
 * }</pre>
 */
public interface DocScraper {
  /** Читает HTML и возвращает список методов. */
  @NonNull List<MethodDoc> scrape(@NonNull InputStream stream);
}
