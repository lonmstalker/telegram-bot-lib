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
package io.lonmstalker.observability.impl;

import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Трейсер, объединяющий сразу несколько Tracer-реализаций. */
public class CompositeTracer implements Tracer {
  private final List<Tracer> delegates;

  public CompositeTracer(@NonNull List<Tracer> delegates) {
    this.delegates = List.copyOf(delegates);
  }

  @Override
  public Span start(@NonNull String name, @NonNull Tags tags) {
    List<Span> spans = delegates.stream().map(t -> t.start(name, tags)).toList();
    return new CompositeSpan(spans);
  }

  /** CompositeSpan: делегирует операции всем вложенным спанам. */
  private record CompositeSpan(List<Span> spans) implements Span {
    private CompositeSpan(@NonNull List<Span> spans) {
      this.spans = spans;
    }

    @Override
    public void setError(@NonNull Throwable error) {
      spans.forEach(s -> s.setError(error));
    }

    @Override
    public void setTag(@NonNull String key, @NonNull String value) {
      spans.forEach(s -> s.setTag(key, value));
    }

    @Override
    public void close() {
      // Закрываем в обратном порядке, на случай вложенных скоупов
      for (int i = spans.size() - 1; i >= 0; i--) {
        spans.get(i).close();
      }
    }
  }
}
