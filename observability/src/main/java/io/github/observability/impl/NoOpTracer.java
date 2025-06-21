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
package io.github.observability.impl;

import io.github.tgkit.observability.Span;
import io.github.tgkit.observability.Tags;
import io.github.tgkit.observability.Tracer;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Трассировщик-заглушка, не сохраняющий информацию о span'ах. */
public final class NoOpTracer implements Tracer {

  /** Возвращает пустой {@link Span}, не совершающий никаких действий. */
  @Override
  public @NonNull Span start(@NonNull String spanName, @NonNull Tags tags) {
    return new Span() {
      @Override
      public void setError(@NonNull Throwable t) {
        // no-op
      }

      @Override
      public void setTag(@NonNull String tag, @NonNull String value) {
        // no-op
      }

      @Override
      public void close() {
        // no-op
      }
    };
  }
}
