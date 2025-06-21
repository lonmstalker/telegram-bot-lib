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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** Реализация {@link DocScraper} на базе Jsoup. */
public class JsoupDocScraper implements DocScraper {

  @Override
  public @NonNull List<MethodDoc> scrape(@NonNull InputStream stream) {
    Document doc;
    try {
      doc = Jsoup.parse(stream, null, "");
    } catch (Exception e) {
      throw new IllegalArgumentException("Не удалось распарсить документ", e);
    }
    Elements methods = doc.select("div.method");
    return methods.parallelStream().map(this::parseMethod).toList();
  }

  private MethodDoc parseMethod(Element el) {
    String name = el.selectFirst("h3").text();
    String desc = el.selectFirst("p").text();
    return new MethodDoc(name, desc);
  }
}
