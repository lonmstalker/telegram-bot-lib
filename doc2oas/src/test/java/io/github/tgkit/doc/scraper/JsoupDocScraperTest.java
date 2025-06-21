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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsoupDocScraperTest {
  @Test
  void parsesMethodsFromHtml() {
    DocScraper scraper = new JsoupDocScraper();
    InputStream in = getClass().getResourceAsStream("/sample.html");
    List<MethodDoc> methods = scraper.scrape(in);
    assertThat(methods).hasSize(2);
    assertThat(methods.get(0).name()).isEqualTo("getMe");
    assertThat(methods.get(1).name()).isEqualTo("sendMessage");
  }

  @Test
  void failsOnBadHtml() {
    DocScraper scraper = new JsoupDocScraper();
    InputStream in =
        new InputStream() {
          @Override
          public int read() throws IOException {
            throw new IOException("boom");
          }
        };
    assertThatThrownBy(() -> scraper.scrape(in)).isInstanceOf(IllegalArgumentException.class);
  }
}
